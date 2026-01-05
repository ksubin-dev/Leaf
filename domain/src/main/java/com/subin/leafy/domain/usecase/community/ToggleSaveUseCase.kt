package com.subin.leafy.domain.usecase.community

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.CommunityRepository

class ToggleSaveUseCase(
    private val repository: CommunityRepository
) {
    suspend operator fun invoke(postId: String, currentStatus: Boolean): DataResourceResult<Unit> {
        return repository.toggleSave(postId, currentStatus)
    }
}