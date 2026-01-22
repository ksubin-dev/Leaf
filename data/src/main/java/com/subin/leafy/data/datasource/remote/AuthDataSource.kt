package com.subin.leafy.data.datasource.remote

import com.subin.leafy.domain.common.DataResourceResult

interface AuthDataSource {
    fun getCurrentUserId(): String?
    fun isUserLoggedIn(): Boolean

    suspend fun login(email: String, password: String): DataResourceResult<Unit>
    suspend fun signUp(email: String, password: String): DataResourceResult<Unit>

    suspend fun logout()
    suspend fun deleteAuthAccount(): DataResourceResult<Unit>

    suspend fun sendPasswordResetEmail(email: String): DataResourceResult<Unit>
}