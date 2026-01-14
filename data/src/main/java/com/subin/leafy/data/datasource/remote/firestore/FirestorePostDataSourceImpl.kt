package com.subin.leafy.data.datasource.remote.firestore

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.subin.leafy.data.datasource.remote.PostDataSource
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.FIELD_COMMENT_COUNT
import com.subin.leafy.data.mapper.toDomain
import com.subin.leafy.data.mapper.toDto
import com.subin.leafy.data.model.dto.CommentDto
import com.subin.leafy.data.model.dto.CommunityPostDto
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Comment
import com.subin.leafy.domain.model.CommunityPost
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

    // --- 1. 피드 조회 (Read) ---

    // 이번 주 인기 노트 (최근 7일 + 조회수 기준)
    override fun getPopularPosts(): Flow<DataResourceResult<List<CommunityPost>>> = callbackFlow {
        // 1. 7일 전 날짜 계산
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val oneWeekAgo = calendar.timeInMillis

        // 2. 쿼리 작성 (시간 필터 + 정렬)
        // [주의] 이 쿼리는 Firestore 콘솔에서 '복합 인덱스' 생성이 필요합니다!
        // (createdAt Ascending/Descending + viewCount Descending)
        val query = postsCollection
            .whereGreaterThan(FirestoreConstants.FIELD_CREATED_AT, oneWeekAgo) // 7일 이내
            .orderBy(FirestoreConstants.FIELD_VIEW_COUNT, Query.Direction.DESCENDING) // 조회수 순
            .limit(20)

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

    // 명예의 전당
    override fun getMostBookmarkedPosts(): Flow<DataResourceResult<List<CommunityPost>>> = callbackFlow {
        val query = postsCollection
            .orderBy(FirestoreConstants.FIELD_BOOKMARK_COUNT, Query.Direction.DESCENDING)
            .limit(20)

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

    // 팔로잉 피드 (인원 제한 없이 모두 보기)
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

    // ID 리스트로 글 목록 가져오기
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

    // 특정 유저 글 모아보기 (프로필)
    override fun getUserPosts(userId: String): Flow<DataResourceResult<List<CommunityPost>>> = callbackFlow {
        // [주의] 여기도 복합 인덱스 필요 (authorId + createdAt)
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

    // 글 상세 조회
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


    // --- 2. 글 작성/수정/삭제 (CRUD) ---
    override suspend fun createPost(post: CommunityPost): DataResourceResult<Unit> {
        return try {

            val postId = post.id.ifEmpty { postsCollection.document().id }

            val docRef = postsCollection.document(postId)

            val dto = post.toDto().copy(id = postId)

            docRef.set(dto).await()

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
            postsCollection.document(postId).delete().await()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }


    // --- 3. 댓글 (SubCollection) ---

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

            val newCommentRef = commentsCollection.document()
            val generatedId = newCommentRef.id

            val commentDto = comment.toDto().copy(id = generatedId)

            firestore.runTransaction { transaction ->
                transaction.set(newCommentRef, commentDto)
                transaction.update(postRef, FIELD_COMMENT_COUNT, FieldValue.increment(1))
            }.await()

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


    // --- 4. 소셜 액션 (Transaction) ---

    override suspend fun toggleLike(postId: String, isLiked: Boolean): DataResourceResult<Unit> {
        return try {
            val postRef = postsCollection.document(postId)
            val increment = if (isLiked) 1L else -1L

            postRef.update(FirestoreConstants.FIELD_LIKE_COUNT, FieldValue.increment(increment)).await()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun toggleBookmark(postId: String, isBookmarked: Boolean): DataResourceResult<Unit> {
        return try {
            val postRef = postsCollection.document(postId)
            val increment = if (isBookmarked) 1L else -1L

            postRef.update(FirestoreConstants.FIELD_BOOKMARK_COUNT, FieldValue.increment(increment)).await()
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

    // [검색 기능 구현]
    override suspend fun searchPosts(query: String): DataResourceResult<List<CommunityPost>> {
        if (query.isBlank()) return DataResourceResult.Success(emptyList())

        return try {
            val collection = postsCollection

            // 1. 태그 검색 (#으로 시작할 경우)
            val querySnapshot = if (query.startsWith("#")) {
                val tag = query.removePrefix("#")
                collection
                    .whereArrayContains(FirestoreConstants.FIELD_TAGS, tag)
                    .orderBy(FirestoreConstants.FIELD_CREATED_AT, Query.Direction.DESCENDING)
                    .get()
                    .await()
            }
            // 2. 제목 검색 (일반 단어)
            else {
                collection
                    .whereGreaterThanOrEqualTo(FirestoreConstants.FIELD_TITLE, query)
                    .whereLessThanOrEqualTo(FirestoreConstants.FIELD_TITLE, query + "\uf8ff")
                    .get()
                    .await()
            }

            val posts = querySnapshot.documents.mapNotNull {
                it.toObject<CommunityPostDto>()?.toDomain()
            }
            DataResourceResult.Success(posts)

        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    // 랭킹용 데이터 조회
    // teaType이 null이면 '전체(이번 주)' 탭, 값이 있으면 '녹차', '홍차' 탭
    override fun getWeeklyRanking(teaType: TeaType?): Flow<DataResourceResult<List<CommunityPost>>> = callbackFlow {

        // 1. 기간 설정 (최근 7일)
        val oneWeekInMillis = 1000L * 60 * 60 * 24 * 7
        val oneWeekAgo = System.currentTimeMillis() - oneWeekInMillis

        // 2. 쿼리 구성
        var query = postsCollection
            .whereGreaterThan(FirestoreConstants.FIELD_CREATED_AT, oneWeekAgo)
            .orderBy(FirestoreConstants.FIELD_VIEW_COUNT, Query.Direction.DESCENDING)
            .limit(10)

        // 3. 차 종류 필터링
        if (teaType != null) {
            query = query.whereEqualTo(FirestoreConstants.FIELD_TEA_TYPE, teaType.name)
        }

        // 4. 실행
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
}