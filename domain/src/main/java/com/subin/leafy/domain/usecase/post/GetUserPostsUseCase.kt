package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// 프로필 (특정 유저 글 모아보기)
class GetUserPostsUseCase(
    private val postRepository: PostRepository
) {
    operator fun invoke(userId: String): Flow<DataResourceResult<List<CommunityPost>>> {
        if (userId.isBlank()) {
            return flowOf(DataResourceResult.Failure(Exception("유효하지 않은 유저 ID입니다.")))
        }
        return postRepository.getUserPosts(userId)
    }
}