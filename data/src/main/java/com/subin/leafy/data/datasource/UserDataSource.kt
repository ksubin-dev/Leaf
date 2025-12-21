package com.subin.leafy.data.datasource

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserStats
import com.subin.leafy.domain.model.id.UserId

interface UserDataSource {
    suspend fun getCurrentUserId(): UserId
    suspend fun getUser(userId: UserId): DataResourceResult<User>
    suspend fun getUserStats(userId: UserId): DataResourceResult<UserStats>
}