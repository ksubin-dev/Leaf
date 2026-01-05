package com.subin.leafy.data.datasource

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface UserDataSource {
    suspend fun getCurrentUserId(): String?
    suspend fun getUser(userId: String): DataResourceResult<User>

    fun getUserFlow(userId: String): Flow<DataResourceResult<User>>
    suspend fun getUserStats(userId: String): DataResourceResult<UserStats>
    suspend fun updateUser(user: User): DataResourceResult<Unit>

    suspend fun followUser(myId: String, targetUserId: String): DataResourceResult<Unit>
    suspend fun unfollowUser(myId: String, targetUserId: String): DataResourceResult<Unit>
    suspend fun checkFollowStatus(myId: String, targetUserId: String): Boolean
    suspend fun fetchTopUsers(limit: Int): DataResourceResult<List<User>>
}