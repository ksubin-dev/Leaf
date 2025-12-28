package com.subin.leafy.domain.usecase.auth

import com.subin.leafy.domain.model.AuthUser
import com.subin.leafy.domain.repository.AuthRepository

class GetAuthUserUseCase(
    private val repo: AuthRepository
) {
    operator fun invoke(): AuthUser? {
        return repo.getCurrentUser()
    }
}