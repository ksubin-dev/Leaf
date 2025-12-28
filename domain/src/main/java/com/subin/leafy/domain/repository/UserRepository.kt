package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    /** 현재 로그인된 유저 ID 조회 (로그인 안 된 경우 null) */
    suspend fun getCurrentUserId(): String?

    fun getUser(userId: String): Flow<DataResourceResult<User>>
    fun updateProfile(user: User): Flow<DataResourceResult<Unit>>
}