package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.UserDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.UserStats
import com.subin.leafy.domain.model.id.UserId
import com.subin.leafy.domain.repository.UserStatsRepository

class UserStatsRepositoryImpl(
    private val dataSource: UserDataSource
) : UserStatsRepository {

    override suspend fun getUserStats(userId: UserId): UserStats {
        val result = dataSource.getUserStats(userId)

        return when (result) {
            is DataResourceResult.Success -> result.data
            is DataResourceResult.Failure -> throw result.exception
            else -> throw Exception("Unknown error fetching stats")
        }
    }
}