package com.subin.leafy.data.datasource

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserStats

interface UserDataSource {
    suspend fun getCurrentUserId(): String?
    suspend fun getUser(userId: String): DataResourceResult<User>
    suspend fun getUserStats(userId: String): DataResourceResult<UserStats>
}