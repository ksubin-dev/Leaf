package com.subin.leafy.data.datasource.remote

import com.subin.leafy.domain.common.DataResourceResult

interface AuthDataSource {
    // 1. 현재 상태 확인
    fun getCurrentUserId(): String?
    fun isUserLoggedIn(): Boolean

    // 2. 핵심 인증 기능 (이메일/비밀번호)
    suspend fun login(email: String, password: String): DataResourceResult<Unit>
    suspend fun signUp(email: String, password: String): DataResourceResult<Unit>

    // 3. 계정 관리
    suspend fun logout()
    suspend fun deleteAuthAccount(): DataResourceResult<Unit>

    // 4. 비밀번호 찾기
    suspend fun sendPasswordResetEmail(email: String): DataResourceResult<Unit>
}