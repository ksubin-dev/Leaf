package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.PostDataSource
import com.subin.leafy.data.datasource.remote.TeaMasterDataSource
import com.subin.leafy.data.datasource.remote.UserDataSource
import com.subin.leafy.data.mapper.toRankingItem
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.*
import com.subin.leafy.domain.repository.PostRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class PostRepositoryImpl(
    private val authDataSource: AuthDataSource,
    private val postDataSource: PostDataSource,
    private val userDataSource: UserDataSource,
    private val teaMasterDataSource: TeaMasterDataSource
) : PostRepository {

    override fun getWeeklyRanking(teaType: TeaType?): Flow<DataResourceResult<List<RankingItem>>> {
        return postDataSource.getWeeklyRanking(teaType).map { result ->
            when (result) {
                is DataResourceResult.Success -> {
                    val filteredPosts = result.data.filter { it.teaType != TeaType.ETC }
                    val top3Posts = filteredPosts.take(3)
                    val rankingItems = top3Posts.mapIndexed { index, post ->
                        post.toRankingItem(rank = index + 1)
                    }

                    DataResourceResult.Success(rankingItems)
                }
                is DataResourceResult.Failure -> {
                    DataResourceResult.Failure(result.exception)
                }
                else -> {
                    DataResourceResult.Failure(Exception("Unknown State"))
                }
            }
        }
    }

    override fun getPopularPosts(): Flow<DataResourceResult<List<CommunityPost>>> {
        return postDataSource.getPopularPosts().map { result ->
            mapPostsWithMyState(result)
        }
    }

    override fun getMostBookmarkedPosts(): Flow<DataResourceResult<List<CommunityPost>>> {
        return postDataSource.getMostBookmarkedPosts().map { result ->
            mapPostsWithMyState(result)
        }
    }

    override fun getFollowingFeed(followingIds: List<String>): Flow<DataResourceResult<List<CommunityPost>>> {

        if (followingIds.isEmpty()) {
            return flowOf(DataResourceResult.Success(emptyList()))
        }

        return postDataSource.getFollowingFeed(followingIds).map { result ->
            mapPostsWithMyState(result)
        }
    }

    override fun getUserPosts(userId: String): Flow<DataResourceResult<List<CommunityPost>>> {
        return postDataSource.getUserPosts(userId).map { result ->
            mapPostsWithMyState(result)
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

    override suspend fun searchPosts(query: String): DataResourceResult<List<CommunityPost>> {
        val result = postDataSource.searchPosts(query)
        return mapPostsWithMyState(result)
    }

    override fun getRecommendedMasters(): Flow<DataResourceResult<List<TeaMaster>>> {
        val myUid =
            authDataSource.getCurrentUserId() ?: return teaMasterDataSource.getRecommendedMasters()

        return combine(
            teaMasterDataSource.getRecommendedMasters(),
            userDataSource.getFollowingIdsFlow(myUid)
        ) { masterResult, followingResult ->

            if (masterResult !is DataResourceResult.Success) {
                return@combine masterResult
            }

            val masters = masterResult.data

            val followingIds = if (followingResult is DataResourceResult.Success) {
                followingResult.data
            } else {
                emptyList()
            }

            val mappedMasters = masters.map { master ->
                master.copy(isFollowing = followingIds.contains(master.id))
            }

            DataResourceResult.Success(mappedMasters)
        }
    }

    override suspend fun createPost(
        postId: String,
        title: String,
        content: String,
        imageUrls: List<String>,
        teaType: String?,
        rating: Int?,
        tags: List<String>,
        brewingSummary: String?,
        originNoteId: String?
    ): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) {
            return DataResourceResult.Failure(Exception("유저 정보를 불러올 수 없습니다."))
        }
        val me = userResult.data

        val newPost = CommunityPost(
            id = postId,
            author = PostAuthor(me.id, me.nickname, me.profileImageUrl, isFollowing = false),
            imageUrls = imageUrls,
            title = title,
            content = content,
            originNoteId = originNoteId,
            teaType = teaType?.let { runCatching { TeaType.valueOf(it) }.getOrNull() },
            rating = rating,
            tags = tags,
            brewingSummary = brewingSummary,
            stats = PostStatistics(0, 0, 0, 0),
            myState = PostSocialState(false, false),
            createdAt = System.currentTimeMillis()
        )

        return postDataSource.createPost(newPost)
    }

    override suspend fun updatePost(post: CommunityPost): DataResourceResult<Unit> {
        return postDataSource.updatePost(post)
    }

    override suspend fun deletePost(postId: String): DataResourceResult<Unit> {
        return postDataSource.deletePost(postId)
    }

    override fun getComments(postId: String): Flow<DataResourceResult<List<Comment>>> {
        return postDataSource.getComments(postId).map { result ->
            if (result is DataResourceResult.Success) {
                val myUid = authDataSource.getCurrentUserId()
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
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) {
            return DataResourceResult.Failure(Exception("유저 정보를 불러올 수 없습니다."))
        }
        val me = userResult.data

        val newComment = Comment(
            id = "",
            postId = postId,
            author = CommentAuthor(
                id = me.id,
                nickname = me.nickname,
                profileImageUrl = me.profileImageUrl
            ),
            content = content,
            createdAt = System.currentTimeMillis(),
            isMine = true
        )
        return postDataSource.addComment(postId, newComment)
    }

    override suspend fun deleteComment(postId: String, commentId: String): DataResourceResult<Unit> {
        return postDataSource.deleteComment(postId, commentId)
    }

    override suspend fun toggleLike(postId: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) return DataResourceResult.Failure(Exception("User error"))

        val isCurrentlyLiked = userResult.data.likedPostIds.contains(postId)
        val newIsLiked = !isCurrentlyLiked

        val postUpdate = postDataSource.toggleLike(postId, newIsLiked)
        if (postUpdate is DataResourceResult.Failure) return postUpdate

        return userDataSource.toggleLikePost(myUid, postId, newIsLiked)
    }

    override suspend fun toggleBookmark(postId: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) return DataResourceResult.Failure(Exception("User error"))

        val isCurrentlyBookmarked = userResult.data.bookmarkedPostIds.contains(postId)
        val newIsBookmarked = !isCurrentlyBookmarked

        val postUpdate = postDataSource.toggleBookmark(postId, newIsBookmarked)
        if (postUpdate is DataResourceResult.Failure) return postUpdate

        return userDataSource.toggleBookmarkPost(myUid, postId, newIsBookmarked)
    }

    override suspend fun incrementViewCount(postId: String): DataResourceResult<Unit> {
        return postDataSource.incrementViewCount(postId)
    }

    override fun getMyLikedPosts(): Flow<DataResourceResult<List<CommunityPost>>> = flow {
        val myUid = authDataSource.getCurrentUserId()
        if (myUid == null) {
            emit(DataResourceResult.Failure(Exception("로그인이 필요합니다.")))
            return@flow
        }

        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) {
            emit(DataResourceResult.Failure(Exception("유저 정보를 불러올 수 없습니다.")))
            return@flow
        }

        val likedIds = userResult.data.likedPostIds

        if (likedIds.isEmpty()) {
            emit(DataResourceResult.Success(emptyList()))
        } else {
            val postsResult = postDataSource.getPostsByIds(likedIds)
            emit(mapPostsWithMyState(postsResult))
        }
    }

    override fun getMyBookmarkedPosts(): Flow<DataResourceResult<List<CommunityPost>>> = flow {
        val myUid = authDataSource.getCurrentUserId()
        if (myUid == null) {
            emit(DataResourceResult.Failure(Exception("로그인이 필요합니다.")))
            return@flow
        }

        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) {
            emit(DataResourceResult.Failure(Exception("유저 정보를 불러올 수 없습니다.")))
            return@flow
        }

        val bookmarkedIds = userResult.data.bookmarkedPostIds

        if (bookmarkedIds.isEmpty()) {
            emit(DataResourceResult.Success(emptyList()))
        } else {
            val postsResult = postDataSource.getPostsByIds(bookmarkedIds)
            emit(mapPostsWithMyState(postsResult))
        }
    }

    private suspend fun mapPostsWithMyState(
        result: DataResourceResult<List<CommunityPost>>
    ): DataResourceResult<List<CommunityPost>> {
        if (result !is DataResourceResult.Success) return result

        val posts = result.data
        val myUid = authDataSource.getCurrentUserId() ?: return result
        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) return result

        val me = userResult.data

        val mappedPosts = posts.map { post ->
            post.copy(
                myState = PostSocialState(
                    isLiked = me.likedPostIds.contains(post.id),
                    isBookmarked = me.bookmarkedPostIds.contains(post.id)
                )
            )
        }
        return DataResourceResult.Success(mappedPosts)
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