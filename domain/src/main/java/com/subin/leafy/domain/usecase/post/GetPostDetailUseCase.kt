package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.repository.PostRepository

class GetPostDetailUseCase(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String): DataResourceResult<CommunityPost> {
        if (postId.isBlank()) return DataResourceResult.Failure(Exception("잘못된 게시글 ID입니다."))
        return postRepository.getPostDetail(postId)
    }
}