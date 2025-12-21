package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.model.UserStats
import com.subin.leafy.domain.model.id.UserId
import com.subin.leafy.domain.repository.UserStatsRepository

class GetUserStatsUseCase(
    private val repo: UserStatsRepository
) {
    suspend operator fun invoke(userId: UserId): UserStats = repo.getUserStats(userId)
}