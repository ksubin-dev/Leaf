package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.CommunityDataSource
import com.subin.leafy.data.mapper.toDomain
import com.subin.leafy.data.mapper.toDomainCommentList
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.common.mapData
import com.subin.leafy.domain.model.*
import com.subin.leafy.domain.repository.AuthRepository
import com.subin.leafy.domain.repository.CommunityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class CommunityRepositoryImpl(
    private val targetDataSource: CommunityDataSource,
    private val authRepository: AuthRepository
) : CommunityRepository {


    private fun applyPostState(posts: List<CommunityPost>, user: AuthUser?): List<CommunityPost> {
        return posts.map { post ->
            post.copy(
                isLiked = user?.likedPostIds?.contains(post.id) == true,
                isBookmarked = user?.savedPostIds?.contains(post.id) == true
            )
        }
    }

    // --- [1. 게시글 조회 관련] ---

    override fun getPopularNotes(): Flow<DataResourceResult<List<CommunityPost>>> =
        authRepository.currentUserState.flatMapLatest { user ->
            targetDataSource.getPopularNotes().map { result ->
                result.mapData { posts -> applyPostState(posts, user) }
            }
        }.flowOn(Dispatchers.IO)

    override fun getMostSavedNotes(): Flow<DataResourceResult<List<CommunityPost>>> =
        authRepository.currentUserState.flatMapLatest { user ->
            targetDataSource.getMostSavedNotes().map { result ->
                result.mapData { posts -> applyPostState(posts, user) }
            }
        }.flowOn(Dispatchers.IO)

    override fun getFollowingFeed(): Flow<DataResourceResult<List<CommunityPost>>> =
        authRepository.currentUserState.flatMapLatest { user ->
            if (user == null) {
                flowOf(DataResourceResult.Failure(Exception("로그인이 필요합니다.")))
            } else if (user.followingIds.isEmpty()) {
                flowOf(DataResourceResult.Success(emptyList()))
            } else {
                targetDataSource.getFollowingFeed(user.followingIds).map { result ->
                    result.mapData { posts -> applyPostState(posts, user) }
                }
            }
        }.flowOn(Dispatchers.IO)

    // --- [2. 마스터 관련] ---

    override fun getRecommendedMasters(): Flow<DataResourceResult<List<TeaMaster>>> =
        authRepository.currentUserState.flatMapLatest { user ->
            targetDataSource.getRecommendedMasters().map { result ->
                result.mapData { masters ->
                    masters.map { master ->
                        master.copy(
                            isFollowing = user?.followingIds?.contains(master.id) == true
                        )
                    }
                }
            }
        }.flowOn(Dispatchers.IO)

    private fun <T> observeDataSource(
        fetcher: () -> Flow<DataResourceResult<T>>
    ): Flow<DataResourceResult<T>> = fetcher().flowOn(Dispatchers.IO)


    // --- [2. 댓글 기능] ---

    override fun getComments(postId: String): Flow<DataResourceResult<List<Comment>>> =
        authRepository.currentUserState.flatMapLatest { user ->
            targetDataSource.getComments(postId).map { result ->
                result.mapData { dtoList ->
                    dtoList.map { dto ->
                        dto.toDomain().copy(
                            isMine = (user != null && dto.authorId == user.id)
                        )
                    }
                }
            }
        }.flowOn(Dispatchers.IO)

    override suspend fun addComment(postId: String, content: String): DataResourceResult<Unit> {
        val user = authRepository.getCurrentUser()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        return targetDataSource.addComment(
            userId = user.id,
            userName = user.username ?: "익명 사용자",
            userProfile = user.profileUrl,
            postId = postId,
            content = content
        )
    }

    override suspend fun deleteComment(commentId: String, postId: String): DataResourceResult<Unit> {
        val user = authRepository.getCurrentUser()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))
        return targetDataSource.deleteComment(commentId, postId)
    }


    // --- [3. 소셜 액션 ] ---

    override suspend fun incrementViewCount(postId: String) =
        targetDataSource.incrementViewCount(postId)

    override suspend fun toggleLike(postId: String, currentStatus: Boolean): DataResourceResult<Unit> {
        val user = authRepository.getCurrentUser() ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val result = targetDataSource.toggleLike(user.id, postId, currentStatus)

        if (result is DataResourceResult.Success) {
            val newLikedIds = if (currentStatus) {
                user.likedPostIds.filter { it != postId }
            } else {
                user.likedPostIds + postId
            }
            authRepository.updateCurrentUserState(likedPostIds = newLikedIds)
        }
        return result
    }

    override suspend fun toggleSave(postId: String, currentStatus: Boolean): DataResourceResult<Unit> {
        val user = authRepository.getCurrentUser() ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val result = targetDataSource.toggleSave(user.id, postId, currentStatus)

        if (result is DataResourceResult.Success) {
            val newSavedIds = if (currentStatus) {
                user.savedPostIds.filter { it != postId }
            } else {
                user.savedPostIds + postId
            }
            authRepository.updateCurrentUserState(savedPostIds = newSavedIds)
        }
        return result
    }

    override suspend fun toggleFollow(masterId: String, currentStatus: Boolean): DataResourceResult<Unit> {
        val user = authRepository.getCurrentUser() ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val result = targetDataSource.toggleFollow(user.id, masterId, currentStatus)

        if (result is DataResourceResult.Success) {
            val newFollowingIds = if (currentStatus) {
                user.followingIds.filter { it != masterId }
            } else {
                user.followingIds + masterId
            }
            authRepository.updateCurrentUserState(followingIds = newFollowingIds)
        }
        return result
    }
}