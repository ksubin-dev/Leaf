package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.PostRepository
import javax.inject.Inject

class DeleteCommentUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String, commentId: String): DataResourceResult<Unit> {
        if (postId.isBlank() || commentId.isBlank()) {
            return DataResourceResult.Failure(Exception("삭제할 대상을 찾을 수 없습니다."))
        }
        return postRepository.deleteComment(postId, commentId)
    }
}