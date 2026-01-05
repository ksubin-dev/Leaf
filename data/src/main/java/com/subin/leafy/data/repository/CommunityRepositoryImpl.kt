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
import kotlinx.coroutines.flow.*

class CommunityRepositoryImpl(
    private val targetDataSource: CommunityDataSource,
    private val authRepository: AuthRepository
) : CommunityRepository {

    private fun composePostStates(posts: List<CommunityPost>): List<CommunityPost> {
        val currentUser = authRepository.getCurrentUser() ?: return posts
        return posts.map { post ->
            post.copy(
                isLiked = currentUser.likedPostIds.contains(post.id),
                isSaved = currentUser.savedPostIds.contains(post.id)
            )
        }
    }

    private fun <T> observeDataSource(
        fetcher: () -> Flow<DataResourceResult<T>>
    ): Flow<DataResourceResult<T>> = fetcher().flowOn(Dispatchers.IO)


    // --- [1. 게시글 조회 관련] ---

    override fun getPopularNotes(): Flow<DataResourceResult<List<CommunityPost>>> = observeDataSource {
        targetDataSource.getPopularNotes().map { result ->
            result.mapData { posts -> composePostStates(posts) }
        }
    }

    override fun getMostSavedNotes(): Flow<DataResourceResult<List<CommunityPost>>> = observeDataSource {
        targetDataSource.getMostSavedNotes().map { result ->
            result.mapData { posts -> composePostStates(posts) }
        }
    }

    override fun getNoteDetail(postId: String): Flow<DataResourceResult<CommunityPost>> = observeDataSource {
        targetDataSource.getNoteDetail(postId).map { result ->
            result.mapData { postDto ->
                val post = postDto.toDomain()
                val currentUser = authRepository.getCurrentUser()
                post.copy(
                    isLiked = currentUser?.likedPostIds?.contains(post.id) ?: false,
                    isSaved = currentUser?.savedPostIds?.contains(post.id) ?: false
                )
            }
        }
    }

    override fun getFollowingFeed(): Flow<DataResourceResult<List<CommunityPost>>> = flow {
        emit(DataResourceResult.Loading)
        val currentUser = authRepository.getCurrentUser()

        if (currentUser == null) {
            emit(DataResourceResult.Failure(Exception("로그인이 필요합니다.")))
            return@flow
        }

        if (currentUser.followingIds.isEmpty()) {
            emit(DataResourceResult.Success(emptyList<CommunityPost>()))
            return@flow
        }

        targetDataSource.getFollowingFeed(currentUser.followingIds)
            .map { result ->
                result.mapData { posts -> composePostStates(posts) }
            }
            .collect { emit(it) }

    }.flowOn(Dispatchers.IO).catch { e ->
        emit(DataResourceResult.Failure(Exception(e.message)))
    }

    override fun getRecommendedMasters() = observeDataSource {
        targetDataSource.getRecommendedMasters()
    }


    // --- [2. 댓글 기능] ---

    override fun getComments(postId: String): Flow<DataResourceResult<List<Comment>>> = observeDataSource {
        targetDataSource.getComments(postId).map { result ->
            result.mapData { dtoList -> dtoList.toDomainCommentList() }
        }
    }

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