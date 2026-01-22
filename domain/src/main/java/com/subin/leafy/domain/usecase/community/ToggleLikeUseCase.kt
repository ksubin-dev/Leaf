package com.subin.leafy.domain.usecase.community

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.CommunityRepository

class ToggleLikeUseCase(private val repository: CommunityRepository) {
    suspend operator fun invoke(postId: String): DataResourceResult<Boolean> {
        return repository.toggleLike(postId)
    }
}