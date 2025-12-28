package com.subin.leafy.domain.usecase.auth

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.AuthUser
import com.subin.leafy.domain.repository.AuthRepository

class LoginUseCase(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): DataResourceResult<AuthUser> {
        return repo.login(email, password)
    }
}