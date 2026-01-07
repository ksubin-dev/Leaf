package com.subin.leafy.domain.usecase.community

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.CommunityRepository

class ToggleFollowUseCase(private val repository: CommunityRepository) {
    suspend operator fun invoke(masterId: String, currentStatus: Boolean): DataResourceResult<Unit> {
        return repository.toggleFollow(masterId, currentStatus)
    }
}