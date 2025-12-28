package com.subin.leafy.data.remote.fakes

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.AuthUser
import com.subin.leafy.domain.repository.AuthRepository
import kotlinx.coroutines.delay

class FakeAuthRepositoryImpl : AuthRepository {
    private var currentUser: AuthUser? = null

    override suspend fun signUp(email: String, password: String, username: String): DataResourceResult<AuthUser> {
        delay(1000) // 통신 시간 흉내
        val newUser = AuthUser(id = "fake_uid_123", email = email, username = username)
        currentUser = newUser
        return DataResourceResult.Success(newUser)
    }

    override suspend fun login(email: String, password: String): DataResourceResult<AuthUser> {
        delay(1000)
        val user = AuthUser(id = "fake_uid_123", email = email, username = "테스트유저")
        currentUser = user
        return DataResourceResult.Success(user)
    }

    override suspend fun logout(): DataResourceResult<Unit> {
        currentUser = null
        return DataResourceResult.Success(Unit)
    }

    override fun getCurrentUser(): AuthUser? = currentUser
}