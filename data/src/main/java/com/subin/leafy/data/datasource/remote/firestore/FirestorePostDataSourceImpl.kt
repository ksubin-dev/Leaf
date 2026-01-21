package com.subin.leafy.data.datasource.remote.firestore

import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.subin.leafy.data.datasource.remote.PostDataSource
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.FIELD_COMMENT_COUNT
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.FIELD_POST_COUNT
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.KEY_BOOKMARK_COUNT
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.KEY_LIKE_COUNT
import com.subin.leafy.data.mapper.toDomain
import com.subin.leafy.data.mapper.toDto
import com.subin.leafy.data.model.dto.CommentDto
import com.subin.leafy.data.model.dto.CommunityPostDto
import com.subin.leafy.data.model.dto.NotificationDto
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Comment
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.NotificationType
import com.subin.leafy.domain.model.RankingPeriod
import com.subin.leafy.domain.model.TeaType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import java.util.Calendar

class FirestorePostDataSourceImpl(
    private val firestore: FirebaseFirestore
) : PostDataSource {

    private val postsCollection = firestore.collection(FirestoreConstants.COLLECTION_POSTS)
    private val usersCollection = firestore.collection(FirestoreConstants.COLLECTION_USERS)

    override fun getPopularPosts(limit: Int): Flow<DataResourceResult<List<CommunityPost>>> = callbackFlow {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val oneWeekAgo = calendar.timeInMillis

        val query = postsCollection
            .whereGreaterThan(FirestoreConstants.FIELD_CREATED_AT, oneWeekAgo)
            .orderBy(FirestoreConstants.FIELD_CREATED_AT, Query.Direction.DESCENDING)
            .orderBy(FirestoreConstants.FIELD_VIEW_COUNT, Query.Direction.DESCENDING)
            .limit(limit.toLong())

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(DataResourceResult.Failure(error))
                return@addSnapshotListener
            }

            val posts = snapshot?.documents?.mapNotNull {
                it.toObject<CommunityPostDto>()?.toDomain()
            } ?: emptyList()

            val sortedPosts = posts.sortedByDescending { it.stats.viewCount }

            trySend(DataResourceResult.Success(sortedPosts))
        }
        awaitClose { listener.remove() }
    }

    override fun getMostBookmarkedPosts(
        period: RankingPeriod,
        limit: Int
    ): Flow<DataResourceResult<List<CommunityPost>>> = callbackFlow {

        val calendar = Calendar.getInstance()
        val startTime = when (period) {
            RankingPeriod.WEEKLY -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                calendar.timeInMillis
            }
            RankingPeriod.MONTHLY -> {
                calendar.add(Calendar.DAY_OF_YEAR, -30)
                calendar.timeInMillis
            }
            RankingPeriod.ALL_TIME -> 0L
        }

        var query = postsCollection
            .orderBy(FirestoreConstants.FIELD_BOOKMARK_COUNT, Query.Direction.DESCENDING)

        if (period != RankingPeriod.ALL_TIME) {
            query = postsCollection
                .whereGreaterThan(FirestoreConstants.FIELD_CREATED_AT, startTime)
                .orderBy(FirestoreConstants.FIELD_BOOKMARK_COUNT, Query.Direction.DESCENDING)
        }

        query = query.limit(limit.toLong())

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(DataResourceResult.Failure(error))
                return@addSnapshotListener
            }
            val posts = snapshot?.documents?.mapNotNull {
                it.toObject<CommunityPostDto>()?.toDomain()
            } ?: emptyList()
            trySend(DataResourceResult.Success(posts))
        }
        awaitClose { listener.remove() }
    }

    override fun getFollowingFeed(followingIds: List<String>): Flow<DataResourceResult<List<CommunityPost>>> {
        if (followingIds.isEmpty()) {
            return flowOf(DataResourceResult.Success(emptyList()))
        }

        val chunks = followingIds.chunked(10)
        val flows = chunks.map { chunk ->
            callbackFlow {
                val query = postsCollection
                    .whereIn(FirestoreConstants.FIELD_AUTHOR_ID, chunk)
                    .orderBy(FirestoreConstants.FIELD_CREATED_AT, Query.Direction.DESCENDING)
                    .limit(20)

                val listener = query.addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList<CommunityPost>())
                        return@addSnapshotListener
                    }
                    val posts = snapshot?.documents?.mapNotNull {
                        it.toObject<CommunityPostDto>()?.toDomain()
                    } ?: emptyList()
                    trySend(posts)
                }
                awaitClose { listener.remove() }
            }
        }

        return combine(flows) { arrayOfLists ->
            val allPosts = arrayOfLists.flatMap { it }
                .sortedByDescending { it.createdAt }
            DataResourceResult.Success(allPosts)
        }
    }

    override suspend fun getPostsByIds(postIds: List<String>): DataResourceResult<List<CommunityPost>> {
        if (postIds.isEmpty()) return DataResourceResult.Success(emptyList())

        return try {
            val postList = mutableListOf<CommunityPost>()
            postIds.chunked(10).forEach { chunk ->
                val snapshot = postsCollection
                    .whereIn(FieldPath.documentId(), chunk)
                    .get()
                    .await()

                val chunkedPosts = snapshot.documents.mapNotNull {
                    it.toObject<CommunityPostDto>()?.toDomain()
                }
                postList.addAll(chunkedPosts)
            }
            DataResourceResult.Success(postList)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override fun getUserPosts(userId: String): Flow<DataResourceResult<List<CommunityPost>>> = callbackFlow {
        val query = postsCollection
            .whereEqualTo(FirestoreConstants.FIELD_AUTHOR_ID, userId)
            .orderBy(FirestoreConstants.FIELD_CREATED_AT, Query.Direction.DESCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(DataResourceResult.Failure(error))
                return@addSnapshotListener
            }
            val posts = snapshot?.documents?.mapNotNull {
                it.toObject<CommunityPostDto>()?.toDomain()
            } ?: emptyList()
            trySend(DataResourceResult.Success(posts))
        }
        awaitClose { listener.remove() }
    }

    override suspend fun getPostDetail(postId: String): DataResourceResult<CommunityPost> {
        return try {
            val snapshot = postsCollection.document(postId).get().await()
            val dto = snapshot.toObject<CommunityPostDto>()
            if (dto != null) {
                DataResourceResult.Success(dto.toDomain())
            } else {
                DataResourceResult.Failure(Exception("Post not found"))
            }
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun createPost(post: CommunityPost): DataResourceResult<Unit> {
        return try {

            val postId = post.id.ifEmpty { postsCollection.document().id }
            val postRef = postsCollection.document(postId)
            val userRef = usersCollection.document(post.author.id)

            val dto = post.toDto().copy(id = postId)

            firestore.runTransaction { transaction ->
                transaction.set(postRef, dto)
                transaction.update(userRef, FIELD_POST_COUNT, FieldValue.increment(1))
            }.await()

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun updatePost(post: CommunityPost): DataResourceResult<Unit> {
        return try {
            val dto = post.toDto()
            postsCollection.document(dto.id)
                .set(dto, SetOptions.merge())
                .await()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun deletePost(postId: String): DataResourceResult<Unit> {
        return try {
            val postRef = postsCollection.document(postId)
            val noteRef = firestore.collection(FirestoreConstants.COLLECTION_NOTES).document(postId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(postRef)

                if (!snapshot.exists()) {
                    throw Exception("Post not found")
                }

                val postDto = snapshot.toObject<CommunityPostDto>()
                val authorId = postDto?.author?.id

                transaction.delete(postRef)

                if (!authorId.isNullOrEmpty()) {
                    val userRef = usersCollection.document(authorId)
                    transaction.update(userRef, FIELD_POST_COUNT, FieldValue.increment(-1))
                }

                val noteSnapshot = transaction.get(noteRef)
                if (noteSnapshot.exists()) {
                    transaction.update(noteRef, FirestoreConstants.FIELD_IS_PUBLIC, false)
                }

            }.await()

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override fun getComments(postId: String): Flow<DataResourceResult<List<Comment>>> = callbackFlow {
        val query = postsCollection.document(postId)
            .collection(FirestoreConstants.COLLECTION_COMMENTS)
            .orderBy(FirestoreConstants.FIELD_CREATED_AT, Query.Direction.ASCENDING)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(DataResourceResult.Failure(error))
                return@addSnapshotListener
            }
            val comments = snapshot?.documents?.mapNotNull {
                it.toObject<CommentDto>()?.toDomain()
            } ?: emptyList()
            trySend(DataResourceResult.Success(comments))
        }
        awaitClose { listener.remove() }
    }

    override suspend fun addComment(postId: String, comment: Comment): DataResourceResult<Unit> {
        return try {
            val postRef = postsCollection.document(postId)
            val commentsCollection = postRef.collection(FirestoreConstants.COLLECTION_COMMENTS)

            val postSnapshot = postRef.get().await()
            val authorMap = postSnapshot.get("author") as? Map<String, Any>
            val postAuthorId = authorMap?.get("id") as? String ?: ""

            val newCommentRef = commentsCollection.document()
            val generatedId = newCommentRef.id
            val commentDto = comment.toDto().copy(id = generatedId)

            firestore.runTransaction { transaction ->
                transaction.set(newCommentRef, commentDto)
                transaction.update(postRef, FIELD_COMMENT_COUNT, FieldValue.increment(1))
            }.await()

            if (postAuthorId.isNotEmpty() && postAuthorId != comment.author.id) {
                sendNotification(
                    targetUserId = postAuthorId,
                    type = NotificationType.COMMENT,
                    senderId = comment.author.id,
                    senderName = comment.author.nickname,
                    senderProfileUrl = comment.author.profileImageUrl,
                    message = "${comment.author.nickname}님이 댓글을 남겼습니다: \"${comment.content}\"",
                    targetPostId = postId
                )
            }

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun deleteComment(postId: String, commentId: String): DataResourceResult<Unit> {
        return try {
            val postRef = postsCollection.document(postId)
            val commentRef = postRef.collection(FirestoreConstants.COLLECTION_COMMENTS).document(commentId)

            firestore.runTransaction { transaction ->
                transaction.delete(commentRef)
                transaction.update(postRef, FIELD_COMMENT_COUNT, FieldValue.increment(-1))
            }.await()

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun toggleLike(postId: String, isLiked: Boolean, userId: String): DataResourceResult<Unit> {
        return try {
            val postSnapshot = postsCollection.document(postId).get().await()
            val authorMap = postSnapshot.get("author") as? Map<String, Any>
            val postAuthorId = authorMap?.get("id") as? String ?: ""

            val userSnapshot = usersCollection.document(userId).get().await()
            val myName = userSnapshot.getString(FirestoreConstants.FIELD_NICKNAME) ?: "알 수 없음"
            val myProfile = userSnapshot.getString(FirestoreConstants.FIELD_PROFILE_IMAGE)

            firestore.runTransaction { transaction ->
                val postRef = postsCollection.document(postId)
                val noteRef = firestore.collection(FirestoreConstants.COLLECTION_NOTES).document(postId)
                val userRef = usersCollection.document(userId)

                val pSnapshot = transaction.get(postRef)
                val nSnapshot = transaction.get(noteRef)

                val increment = if (isLiked) 1L else -1L

                if (pSnapshot.exists()) {
                    transaction.update(postRef, FirestoreConstants.FIELD_LIKE_COUNT, FieldValue.increment(increment))
                }
                if (nSnapshot.exists()) {
                    transaction.update(noteRef, FirestoreConstants.KEY_LIKE_COUNT, FieldValue.increment(increment))
                }

                if (isLiked) {
                    transaction.update(userRef, FirestoreConstants.FIELD_LIKED_POST_IDS, FieldValue.arrayUnion(postId))
                } else {
                    transaction.update(userRef, FirestoreConstants.FIELD_LIKED_POST_IDS, FieldValue.arrayRemove(postId))
                }
            }.await()

            // 3. 좋아요 알림 전송 (좋아요 취소 시엔 안 보냄, 내 글엔 안 보냄)
            if (isLiked && postAuthorId.isNotEmpty() && postAuthorId != userId) {
                sendNotification(
                    targetUserId = postAuthorId,
                    type = NotificationType.LIKE,
                    senderId = userId,
                    senderName = myName,
                    senderProfileUrl = myProfile,
                    message = "${myName}님이 회원님의 게시글을 좋아합니다.",
                    targetPostId = postId
                )
            }

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun toggleBookmark(postId: String, isBookmarked: Boolean, userId: String): DataResourceResult<Unit> {
        return try {
            firestore.runTransaction { transaction ->
                val postRef = postsCollection.document(postId)
                val noteRef = firestore.collection(FirestoreConstants.COLLECTION_NOTES).document(postId)
                val userRef = usersCollection.document(userId)

                val postSnapshot = transaction.get(postRef)
                val noteSnapshot = transaction.get(noteRef)

                val increment = if (isBookmarked) 1L else -1L

                if (postSnapshot.exists()) {
                    transaction.update(postRef, FirestoreConstants.FIELD_BOOKMARK_COUNT, FieldValue.increment(increment))
                }

                if (noteSnapshot.exists()) {
                    transaction.update(noteRef, FirestoreConstants.KEY_BOOKMARK_COUNT, FieldValue.increment(increment))
                }

                if (isBookmarked) {
                    transaction.update(userRef, FirestoreConstants.FIELD_BOOKMARKED_POST_IDS, FieldValue.arrayUnion(postId))
                } else {
                    transaction.update(userRef, FirestoreConstants.FIELD_BOOKMARKED_POST_IDS, FieldValue.arrayRemove(postId))
                }

            }.await()

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun incrementViewCount(postId: String): DataResourceResult<Unit> {
        return try {
            postsCollection.document(postId)
                .update(FirestoreConstants.FIELD_VIEW_COUNT, FieldValue.increment(1))
                .await()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun searchPosts(
        query: String,
        lastPostId: String?,
        limit: Int
    ): DataResourceResult<List<CommunityPost>> {
        if (query.isBlank()) return DataResourceResult.Success(emptyList())

        return try {
            val lastDocument = lastPostId?.let {
                postsCollection.document(it).get().await()
            }

            var firestoreQuery: Query = if (query.startsWith("#")) {
                postsCollection
                    .whereArrayContains(FirestoreConstants.FIELD_TAGS, query)
            } else {
                postsCollection
                    .whereGreaterThanOrEqualTo(FirestoreConstants.FIELD_TITLE, query)
                    .whereLessThanOrEqualTo(FirestoreConstants.FIELD_TITLE, query + "\uf8ff")
                    .orderBy(FirestoreConstants.FIELD_TITLE)
            }

            if (lastDocument != null && lastDocument.exists()) {
                firestoreQuery = firestoreQuery.startAfter(lastDocument)
            }

            val querySnapshot = firestoreQuery.limit(limit.toLong()).get().await()

            val posts = querySnapshot.documents.mapNotNull {
                it.toObject<CommunityPostDto>()?.toDomain()
            }

            val finalPosts = if (query.startsWith("#")) {
                posts.sortedByDescending { it.createdAt }
            } else {
                posts
            }

            DataResourceResult.Success(finalPosts)

        } catch (e: Exception) {
            e.printStackTrace()
            DataResourceResult.Failure(e)
        }
    }

    override fun getWeeklyRanking(teaType: TeaType?): Flow<DataResourceResult<List<CommunityPost>>> = callbackFlow {

        val oneWeekInMillis = 1000L * 60 * 60 * 24 * 7
        val oneWeekAgo = System.currentTimeMillis() - oneWeekInMillis

        var query = postsCollection
            .whereGreaterThan(FirestoreConstants.FIELD_CREATED_AT, oneWeekAgo)
            .orderBy(FirestoreConstants.FIELD_VIEW_COUNT, Query.Direction.DESCENDING)
            .limit(10)

        if (teaType != null) {
            query = query.whereEqualTo(FirestoreConstants.FIELD_TEA_TYPE, teaType.name)
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(DataResourceResult.Failure(error))
                return@addSnapshotListener
            }

            val posts = snapshot?.documents?.mapNotNull {
                it.toObject<CommunityPostDto>()?.toDomain()
            } ?: emptyList()

            trySend(DataResourceResult.Success(posts))
        }
        awaitClose { listener.remove() }
    }

    private fun sendNotification(
        targetUserId: String,
        type: NotificationType,
        senderId: String,
        senderName: String,
        senderProfileUrl: String?,
        message: String,
        targetPostId: String? = null
    ) {
        val notiRef = usersCollection.document(targetUserId)
            .collection(FirestoreConstants.COLLECTION_NOTIFICATIONS)
            .document()

        val notification = NotificationDto(
            id = notiRef.id,
            type = type.name,
            senderId = senderId,
            senderName = senderName,
            senderProfileUrl = senderProfileUrl,
            targetPostId = targetPostId,
            message = message,
            isRead = false,
            createdAt = System.currentTimeMillis()
        )

        notiRef.set(notification)
            .addOnFailureListener { e ->
                Log.e("FirestorePostDataSource", "Failed to send notification: ${e.message}", e)
            }
    }
}