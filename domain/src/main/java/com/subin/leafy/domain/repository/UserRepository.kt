package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getCurrentUserId(): String?

    fun getUser(userId: String): Flow<DataResourceResult<User>>
    fun updateProfile(user: User): Flow<DataResourceResult<Unit>>

    fun toggleFollow(myId: String, targetUserId: String, isFollowing: Boolean): Flow<DataResourceResult<Unit>>
    fun isFollowing(myId: String, targetUserId: String): Flow<DataResourceResult<Boolean>>

    fun getTopUsers(limit: Int): Flow<DataResourceResult<List<User>>>
}