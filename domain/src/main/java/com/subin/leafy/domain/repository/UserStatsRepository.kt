package com.subin.leafy.domain.repository

import com.subin.leafy.domain.model.UserStats
import com.subin.leafy.domain.model.id.UserId

interface UserStatsRepository {
    suspend fun getUserStats(userId: UserId): UserStats
}