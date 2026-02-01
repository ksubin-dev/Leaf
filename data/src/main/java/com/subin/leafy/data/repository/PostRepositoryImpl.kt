package com.subin.leafy.data.repository

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.subin.leafy.data.worker.CommunityUploadWorker
import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.PostDataSource
import com.subin.leafy.data.datasource.remote.TeaMasterDataSource
import com.subin.leafy.data.datasource.remote.UserDataSource
import com.subin.leafy.data.mapper.toRankingItem
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.*
import com.subin.leafy.domain.repository.PostChangeEvent
import com.subin.leafy.domain.repository.PostRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class PostRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val postDataSource: PostDataSource,
    private val userDataSource: UserDataSource,
    private val teaMasterDataSource: TeaMasterDataSource,
    private val workManager: WorkManager
) : PostRepository {

    private val _postChangeFlow = MutableSharedFlow<PostChangeEvent>()
    override val postChangeFlow: Flow<PostChangeEvent> = _postChangeFlow.asSharedFlow()

    override fun getWeeklyRanking(teaType: TeaType?): Flow<DataResourceResult<List<RankingItem>>> {
        return postDataSource.getWeeklyRanking(teaType).map { result ->
            when (result) {
                is DataResourceResult.Success -> {
                    val filteredPosts = result.data.filter { it.teaType != TeaType.ETC }
                    val rankingItems = filteredPosts.mapIndexed { index, post ->
                        post.toRankingItem(rank = index + 1)
                    }

                    DataResourceResult.Success(rankingItems)
                }
                is DataResourceResult.Failure -> DataResourceResult.Failure(result.exception)
                else -> DataResourceResult.Failure(Exception("Unknown State"))
            }
        }
    }

    override fun getPopularPosts(limit: Int): Flow<DataResourceResult<List<CommunityPost>>> {
        return postDataSource.getPopularPosts(limit).combineWithUser()
    }

    override fun getMostBookmarkedPosts(period: RankingPeriod, limit: Int): Flow<DataResourceResult<List<CommunityPost>>> {
        return postDataSource.getMostBookmarkedPosts(period, limit).combineWithUser()
    }

    override fun getFollowingFeed(followingIds: List<String>): Flow<DataResourceResult<List<CommunityPost>>> {
        if (followingIds.isEmpty()) return flowOf(DataResourceResult.Success(emptyList()))
        return postDataSource.getFollowingFeed(followingIds).combineWithUser()
    }

    override fun getUserPosts(userId: String): Flow<DataResourceResult<List<CommunityPost>>> {
        return postDataSource.getUserPosts(userId).combineWithUser()
    }

    override fun getMyLikedPosts(): Flow<DataResourceResult<List<CommunityPost>>> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return flowOf(DataResourceResult.Failure(Exception("로그인이 필요합니다.")))

        return userDataSource.getUserFlow(myUid).flatMapLatest { userResult ->
            if (userResult is DataResourceResult.Success) {
                val likedIds = userResult.data.likedPostIds

                if (likedIds.isEmpty()) {
                    flowOf(DataResourceResult.Success(emptyList()))
                } else {
                    flow {
                        val postsResult = postDataSource.getPostsByIds(likedIds)
                        emit(mapPostsWithMyStateInternal(postsResult, userResult.data))
                    }
                }
            } else {
                flowOf(DataResourceResult.Failure(Exception("유저 정보 로드 실패")))
            }
        }
    }

    override fun getMyBookmarkedPosts(): Flow<DataResourceResult<List<CommunityPost>>> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return flowOf(DataResourceResult.Failure(Exception("로그인이 필요합니다.")))

        return userDataSource.getUserFlow(myUid).flatMapLatest { userResult ->
            if (userResult is DataResourceResult.Success) {
                val bookmarkedIds = userResult.data.bookmarkedPostIds

                if (bookmarkedIds.isEmpty()) {
                    flowOf(DataResourceResult.Success(emptyList()))
                } else {
                    flow {
                        val postsResult = postDataSource.getPostsByIds(bookmarkedIds)
                        emit(mapPostsWithMyStateInternal(postsResult, userResult.data))
                    }
                }
            } else {
                flowOf(DataResourceResult.Failure(Exception("유저 정보 로드 실패")))
            }
        }
    }

    override suspend fun getPostDetail(postId: String): DataResourceResult<CommunityPost> {
        val result = postDataSource.getPostDetail(postId)
        return if (result is DataResourceResult.Success) {
            val mappedPost = mapSinglePostWithMyState(result.data)
            DataResourceResult.Success(mappedPost)
        } else {
            result
        }
    }

    override suspend fun searchPosts(
        query: String,
        lastPostId: String?,
        limit: Int
    ): DataResourceResult<List<CommunityPost>> {
        val result = postDataSource.searchPosts(query, lastPostId, limit)
        return mapPostsWithMyStateSuspend(result)
    }

    override fun getRecommendedMasters(limit: Int): Flow<DataResourceResult<List<TeaMaster>>> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return teaMasterDataSource.getRecommendedMasters(limit)

        return combine(
            teaMasterDataSource.getRecommendedMasters(limit),
            userDataSource.getFollowingIdsFlow(myUid)
        ) { masterResult, followingResult ->
            if (masterResult !is DataResourceResult.Success) return@combine masterResult

            val masters = masterResult.data
            val followingIds = if (followingResult is DataResourceResult.Success) followingResult.data else emptyList()

            val mappedMasters = masters.map { master ->
                master.copy(isFollowing = followingIds.contains(master.id))
            }
            DataResourceResult.Success(mappedMasters)
        }
    }

    override suspend fun createPost(
        postId: String, title: String, content: String, imageUrls: List<String>,
        teaType: String?, rating: Int?, tags: List<String>, brewingSummary: String?, originNoteId: String?
    ): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId() ?: return DataResourceResult.Failure(Exception("로그인 필요"))
        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) return DataResourceResult.Failure(Exception("유저 정보 오류"))
        val me = userResult.data

        val newPost = CommunityPost(
            id = postId,
            author = PostAuthor(me.id, me.nickname, me.profileImageUrl, isFollowing = false),
            imageUrls = imageUrls, title = title, content = content, originNoteId = originNoteId,
            teaType = teaType?.let { runCatching { TeaType.valueOf(it) }.getOrNull() },
            rating = rating, tags = tags, brewingSummary = brewingSummary,
            stats = PostStatistics(0, 0, 0, 0),
            myState = PostSocialState(false, false),
            createdAt = System.currentTimeMillis()
        )
        return postDataSource.createPost(newPost)
    }

    override suspend fun updatePost(post: CommunityPost) = postDataSource.updatePost(post)
    override suspend fun deletePost(postId: String) = postDataSource.deletePost(postId)

    override fun getComments(postId: String): Flow<DataResourceResult<List<Comment>>> {
        val myUid = authDataSource.getCurrentUserId()
        return postDataSource.getComments(postId).map { result ->
            if (result is DataResourceResult.Success) {
                val mappedComments = result.data.map { comment ->
                    comment.copy(isMine = comment.author.id == myUid)
                }
                DataResourceResult.Success(mappedComments)
            } else {
                result
            }
        }
    }

    override suspend fun addComment(postId: String, content: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId() ?: return DataResourceResult.Failure(Exception("로그인 필요"))
        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) return DataResourceResult.Failure(Exception("유저 정보 오류"))
        val me = userResult.data

        val newComment = Comment(
            id = "", postId = postId,
            author = CommentAuthor(me.id, me.nickname, me.profileImageUrl),
            content = content, createdAt = System.currentTimeMillis(), isMine = true
        )
        return postDataSource.addComment(postId, newComment)
    }

    override suspend fun deleteComment(postId: String, commentId: String) = postDataSource.deleteComment(postId, commentId)

    override suspend fun toggleLike(postId: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) return DataResourceResult.Failure(Exception("User error"))

        val isCurrentlyLiked = userResult.data.likedPostIds.contains(postId)
        val newIsLiked = !isCurrentlyLiked

        val result = postDataSource.toggleLike(postId, newIsLiked, myUid)

        if (result is DataResourceResult.Success) {
            _postChangeFlow.emit(PostChangeEvent.Like(postId, newIsLiked))
        }

        return result
    }

    override suspend fun toggleBookmark(postId: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) return DataResourceResult.Failure(Exception("User error"))

        val isCurrentlyBookmarked = userResult.data.bookmarkedPostIds.contains(postId)
        val newIsBookmarked = !isCurrentlyBookmarked

        val result = postDataSource.toggleBookmark(postId, newIsBookmarked, myUid)

        if (result is DataResourceResult.Success) {
            _postChangeFlow.emit(PostChangeEvent.Bookmark(postId, newIsBookmarked))
        }

        return result
    }

    override suspend fun incrementViewCount(postId: String) = postDataSource.incrementViewCount(postId)

    private fun Flow<DataResourceResult<List<CommunityPost>>>.combineWithUser(): Flow<DataResourceResult<List<CommunityPost>>> {
        val myUid = authDataSource.getCurrentUserId() ?: return this

        return combine(this, userDataSource.getUserFlow(myUid)) { postResult, userResult ->
            if (postResult is DataResourceResult.Success && userResult is DataResourceResult.Success) {
                mapPostsWithMyStateInternal(postResult, userResult.data)
            } else {
                postResult
            }
        }
    }

    override fun schedulePostUpload(
        title: String,
        content: String,
        tags: List<String>,
        imageUriStrings: List<String>,
        linkedNoteId: String?,
        linkedTeaType: String?,
        linkedRating: Int?
    ) {
        val inputData = workDataOf(
            CommunityUploadWorker.KEY_TITLE to title,
            CommunityUploadWorker.KEY_CONTENT to content,
            CommunityUploadWorker.KEY_TAGS to tags.toTypedArray(),
            CommunityUploadWorker.KEY_IMAGE_URIS to imageUriStrings.toTypedArray(),

            CommunityUploadWorker.KEY_LINKED_NOTE_ID to linkedNoteId,
            CommunityUploadWorker.KEY_LINKED_TEA_TYPE to linkedTeaType,
            CommunityUploadWorker.KEY_LINKED_RATING to (linkedRating ?: -1)
        )

        val uploadRequest = OneTimeWorkRequestBuilder<CommunityUploadWorker>()
            .setInputData(inputData)
            .build()

        workManager.enqueue(uploadRequest)
    }

    private fun mapPostsWithMyStateInternal(
        postResult: DataResourceResult<List<CommunityPost>>,
        me: User
    ): DataResourceResult<List<CommunityPost>> {
        if (postResult !is DataResourceResult.Success) return postResult

        val mappedPosts = postResult.data.map { post ->
            post.copy(
                myState = PostSocialState(
                    isLiked = me.likedPostIds.contains(post.id),
                    isBookmarked = me.bookmarkedPostIds.contains(post.id)
                )
            )
        }
        return DataResourceResult.Success(mappedPosts)
    }

    private suspend fun mapPostsWithMyStateSuspend(
        result: DataResourceResult<List<CommunityPost>>
    ): DataResourceResult<List<CommunityPost>> {
        if (result !is DataResourceResult.Success) return result
        val myUid = authDataSource.getCurrentUserId() ?: return result
        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) return result

        return mapPostsWithMyStateInternal(result, userResult.data)
    }

    private suspend fun mapSinglePostWithMyState(post: CommunityPost): CommunityPost {
        val myUid = authDataSource.getCurrentUserId() ?: return post
        val userResult = userDataSource.getUser(myUid)

        return if (userResult is DataResourceResult.Success) {
            val me = userResult.data
            post.copy(
                myState = PostSocialState(
                    isLiked = me.likedPostIds.contains(post.id),
                    isBookmarked = me.bookmarkedPostIds.contains(post.id)
                )
            )
        } else {
            post
        }
    }
}