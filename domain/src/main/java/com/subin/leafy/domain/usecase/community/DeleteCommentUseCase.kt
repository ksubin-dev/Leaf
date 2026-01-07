package com.subin.leafy.domain.usecase.community

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.CommunityRepository

class DeleteCommentUseCase(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(commentId: String, postId: String): DataResourceResult<Unit> {
        return repository.deleteComment(commentId, postId)
    }
}