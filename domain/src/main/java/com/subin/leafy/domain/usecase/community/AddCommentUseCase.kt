package com.subin.leafy.domain.usecase.community

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.CommunityRepository

class AddCommentUseCase(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(postId: String, content: String): DataResourceResult<Unit> {
        return repository.addComment(postId, content)
    }
}