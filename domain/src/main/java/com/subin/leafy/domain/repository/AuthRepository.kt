package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // 1. 상태 확인
    fun isUserLoggedIn(): Boolean
    fun getCurrentUserId(): String?

    // 2. 이메일/비번 인증
    suspend fun login(email: String, password: String): DataResourceResult<User>
    suspend fun signUp(email: String, password: String, nickname: String, profileImageUri: String? = null): DataResourceResult<User>

    suspend fun checkNicknameAvailability(nickname: String): DataResourceResult<Boolean>

    // 3. 계정 관리
    suspend fun signOut(): DataResourceResult<Unit>
    suspend fun deleteAccount(): DataResourceResult<Unit>

    // 4. 설정
    fun getAutoLoginState(): Flow<Boolean>
    suspend fun setAutoLoginState(enabled: Boolean)
}