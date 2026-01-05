package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.AuthUser

interface AuthRepository {
    suspend fun signUp(email: String, password: String, username: String, profileImageUri: String?): DataResourceResult<AuthUser>
    suspend fun login(email: String, password: String): DataResourceResult<AuthUser>
    suspend fun logout(): DataResourceResult<Unit>
    fun getCurrentUser(): AuthUser?

    fun updateCurrentUserState(
        likedPostIds: List<String>? = null,
        savedPostIds: List<String>? = null,
        followingIds: List<String>? = null
    )
}