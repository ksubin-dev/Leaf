package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun isUserLoggedIn(): Boolean
    fun getCurrentUserId(): String?

    suspend fun login(email: String, password: String): DataResourceResult<User>
    suspend fun signUp(email: String, password: String, nickname: String, profileImageUri: String? = null): DataResourceResult<User>

    suspend fun checkNicknameAvailability(nickname: String): DataResourceResult<Boolean>

    suspend fun signOut(): DataResourceResult<Unit>
    suspend fun deleteAccount(): DataResourceResult<Unit>

    fun getAutoLoginState(): Flow<Boolean>
    suspend fun setAutoLoginState(enabled: Boolean)
}