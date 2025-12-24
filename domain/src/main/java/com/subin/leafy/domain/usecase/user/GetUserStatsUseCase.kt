package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.UserStats
import com.subin.leafy.domain.repository.UserStatsRepository
import kotlinx.coroutines.flow.Flow

class GetUserStatsUseCase(
    private val repo: UserStatsRepository
) {
    operator fun invoke(userId: String): Flow<DataResourceResult<UserStats>> {
        return repo.getUserStats(userId)
    }
}