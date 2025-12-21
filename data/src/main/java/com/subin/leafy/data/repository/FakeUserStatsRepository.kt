package com.subin.leafy.data.repository

import com.subin.leafy.domain.model.UserStats
import com.subin.leafy.domain.model.id.UserId
import com.subin.leafy.domain.repository.UserStatsRepository

class FakeUserStatsRepository : UserStatsRepository {

    private val mockStats = UserStats(
        weeklyBrewingCount = 3,
        averageRating = 4.5,
        preferredTea = "Oolong",
        averageBrewingTime = "3:00",
        monthlyBrewingCount = 12
    )

    override suspend fun getUserStats(userId: UserId): UserStats {
        return mockStats
    }
}