package com.subin.leafy.data.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.leafy.shared.ui.utils.LeafyTimeUtils
import com.subin.leafy.data.datasource.CommunityDataSource
import com.subin.leafy.data.mapper.*
import com.subin.leafy.data.model.dto.*
import com.subin.leafy.data.model.dto.CommunityPostDTO
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await

class FirestoreCommunityDataSourceImpl(
    private val firestore: FirebaseFirestore
) : CommunityDataSource {

    companion object {
        const val COL_POSTS = "brewing_notes"
        const val COL_MASTERS = "tea_masters"
        const val COL_COMMENTS = "comments"
        const val COL_LIKES = "likes"
        const val COL_BOOKMARKS = "bookmarks"
        const val COL_FOLLOWS = "follows"
    }

    private val postsCol = firestore.collection(COL_POSTS)
    private val mastersCol = firestore.collection(COL_MASTERS)
    private val commentsCol = firestore.collection(COL_COMMENTS)

    /** 공통 Snapshot Flow 헬퍼 */
    private fun <T, R> CollectionReference.asSnapshotFlow(
        queryCustomizer: (Query) -> Query = { it },
        dtoClass: Class<T>,
        mapper: (List<T>) -> List<R>
    ): Flow<DataResourceResult<List<R>>> = callbackFlow {
        val subscription = queryCustomizer(this@asSnapshotFlow)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResourceResult.Failure(error))
                    return@addSnapshotListener
                }
                snapshot?.let {
                    val dtos = it.toObjects(dtoClass)
                    trySend(DataResourceResult.Success(mapper(dtos)))
                }
            }
        awaitClose { subscription.remove() }
    }.onStart { emit(DataResourceResult.Loading) }

    private suspend fun getFallbackPosts(limit: Long): List<CommunityPostDTO> {
        return postsCol.orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()
            .toObjects(CommunityPostDTO::class.java)
    }

    // --- [1. 커뮤니티 게시글 조회 관련] ---

    override fun getPopularNotes() = callbackFlow {
        val subscription = postsCol
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResourceResult.Failure(error))
                    return@addSnapshotListener
                }
                val dtos = snapshot?.toObjects(BrewingNoteDTO::class.java) ?: emptyList()

                if (dtos.size < 3) {
                    postsCol.orderBy("createdAt", Query.Direction.DESCENDING).limit(10).get()
                        .addOnSuccessListener { fallbackSnapshot ->
                            val fallbackDtos = fallbackSnapshot.toObjects(BrewingNoteDTO::class.java)
                            trySend(DataResourceResult.Success(fallbackDtos.toDomainListFromBrewing()))
                        }
                } else {
                    trySend(DataResourceResult.Success(dtos.toDomainListFromBrewing()))
                }
            }
        awaitClose { subscription.remove() }
    }.onStart { emit(DataResourceResult.Loading) }



    override fun getMostSavedNotes(): Flow<DataResourceResult<List<CommunityPost>>> = callbackFlow {
        val subscription = postsCol
            .orderBy("bookmarkCount", Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResourceResult.Failure(error))
                    return@addSnapshotListener
                }

                val dtos = snapshot?.toObjects(BrewingNoteDTO::class.java) ?: emptyList()

                if (dtos.size < 2) {
                    postsCol.orderBy("createdAt", Query.Direction.DESCENDING)
                        .limit(10)
                        .get()
                        .addOnSuccessListener { fallbackSnapshot ->
                            val fallbackDtos = fallbackSnapshot.toObjects(BrewingNoteDTO::class.java)
                            trySend(DataResourceResult.Success(fallbackDtos.toDomainListFromBrewing()))
                        }
                        .addOnFailureListener { trySend(DataResourceResult.Failure(it)) }
                } else {
                    trySend(DataResourceResult.Success(dtos.toDomainListFromBrewing()))
                }
            }
        awaitClose { subscription.remove() }
    }.onStart { emit(DataResourceResult.Loading) }

    override fun getFollowingFeed(followingIds: List<String>): Flow<DataResourceResult<List<CommunityPost>>> = callbackFlow {
        if (followingIds.isEmpty()) {
            trySend(DataResourceResult.Success(emptyList()))
            return@callbackFlow
        }

        val subscription = postsCol
            .whereIn("userId", followingIds)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResourceResult.Failure(error))
                    return@addSnapshotListener
                }
                val dtos = snapshot?.toObjects(BrewingNoteDTO::class.java) ?: emptyList()
                trySend(DataResourceResult.Success(dtos.toDomainListFromBrewing()))
            }
        awaitClose { subscription.remove() }
    }.onStart { emit(DataResourceResult.Loading) }

    // --- [2. 티 마스터] ---

    override fun getRecommendedMasters() = callbackFlow {
        val subscription = mastersCol.limit(10)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(DataResourceResult.Failure(error))
                    return@addSnapshotListener
                }

                val dtos = snapshot?.toObjects(TeaMasterDTO::class.java) ?: emptyList()
                if (dtos.isEmpty()) {
                    firestore.collection("users")
                        .orderBy("createdAt", Query.Direction.DESCENDING)
                        .limit(10)
                        .get()
                        .addOnSuccessListener { userSnapshot ->
                            val fallbackMasters = userSnapshot.documents.map { doc ->
                                TeaMasterDTO(
                                    id = doc.id,
                                    name = doc.getString("username") ?: "신규 마스터",
                                    title = "취향을 기록하는 티 러버",
                                    profileImageUrl = doc.getString("profileUrl"),
                                    isFollowing = false
                                )
                            }
                            trySend(DataResourceResult.Success(fallbackMasters.map { it.toDomain() }))
                        }
                        .addOnFailureListener {
                            trySend(DataResourceResult.Failure(it))
                        }
                } else {
                    trySend(DataResourceResult.Success(dtos.map { it.toDomain() }))
                }
            }
        awaitClose { subscription.remove() }
    }.onStart { emit(DataResourceResult.Loading) }


    // --- [3. 댓글 기능 관련] ---

    override fun getComments(postId: String): Flow<DataResourceResult<List<CommentDTO>>> =
        commentsCol.asSnapshotFlow(
            queryCustomizer = {
                it.whereEqualTo("postId", postId)
                    .orderBy("createdAt", Query.Direction.ASCENDING)
            },
            dtoClass = CommentDTO::class.java,
            mapper = { it } // DTO 리스트 그대로 반환 (Mapper는 Repository나 UI에서 사용)
        )

    override suspend fun addComment(
        userId: String,
        userName: String,
        userProfile: String?,
        postId: String,
        content: String
    ): DataResourceResult<Unit> = runCatching {
        val batch = firestore.batch()
        val commentRef = commentsCol.document()

        val commentDTO = CommentDTO(
            id = commentRef.id,
            postId = postId,
            authorId = userId,
            authorName = userName,
            authorProfileUrl = userProfile,
            content = content
        )

        batch.set(commentRef, commentDTO)
        // 게시글의 댓글 수 증가
        batch.update(postsCol.document(postId), "commentCount", FieldValue.increment(1))

        batch.commit().await()
        DataResourceResult.Success(Unit)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun deleteComment(commentId: String, postId: String): DataResourceResult<Unit> = runCatching {
        val batch = firestore.batch()
        batch.delete(commentsCol.document(commentId))
        batch.update(postsCol.document(postId), "commentCount", FieldValue.increment(-1))
        batch.commit().await()
        DataResourceResult.Success(Unit)
    }.getOrElse { DataResourceResult.Failure(it) }

    // --- [4. 소셜 액션 관련 구현] ---
    override suspend fun toggleLike(userId: String, postId: String, isLiked: Boolean): DataResourceResult<Unit> = runCatching {
        val batch = firestore.batch()
        val postRef = postsCol.document(postId)
        val likeRef = firestore.collection(COL_LIKES).document("${userId}_$postId")
        if (isLiked) {
            batch.delete(likeRef)
            batch.update(postRef, "likeCount", FieldValue.increment(-1))
        } else {
            batch.set(likeRef, mapOf(
                "userId" to userId,
                "postId" to postId,
                "timestamp" to FieldValue.serverTimestamp()
            ))
            batch.update(postRef, "likeCount", FieldValue.increment(1))
        }
        batch.commit().await()
        DataResourceResult.Success(Unit)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun toggleSave(userId: String, postId: String, isSaved: Boolean): DataResourceResult<Unit> = runCatching {
        val batch = firestore.batch()
        val postRef = postsCol.document(postId)
        val saveRef = firestore.collection(COL_BOOKMARKS).document("${userId}_$postId")

        if (isSaved) {
            batch.delete(saveRef)
            batch.update(postRef, "bookmarkCount", FieldValue.increment(-1))
        } else {
            batch.set(saveRef, mapOf("userId" to userId, "postId" to postId, "timestamp" to FieldValue.serverTimestamp()))
            batch.update(postRef, "bookmarkCount", FieldValue.increment(1))
        }
        batch.commit().await()
        DataResourceResult.Success(Unit)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun toggleFollow(userId: String, masterId: String, isFollowing: Boolean): DataResourceResult<Unit> = runCatching {
        val batch = firestore.batch()
        val followRef = firestore.collection(COL_FOLLOWS).document("${userId}_$masterId")

        if (isFollowing) {
            batch.delete(followRef)
        } else {
            batch.set(followRef, mapOf("followerId" to userId, "followingId" to masterId, "timestamp" to FieldValue.serverTimestamp()))
        }
        batch.commit().await()
        DataResourceResult.Success(Unit)
    }.getOrElse { DataResourceResult.Failure(it) }

    override suspend fun incrementViewCount(postId: String): DataResourceResult<Unit> = runCatching {
        postsCol.document(postId).update("viewCount", FieldValue.increment(1)).await()
        DataResourceResult.Success(Unit)
    }.getOrElse { DataResourceResult.Failure(it) }
}