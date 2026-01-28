package com.subin.leafy.domain.usecase.auth

import com.subin.leafy.domain.repository.AuthRepository
import javax.inject.Inject

class CheckLoginStatusUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): String? {
        return authRepository.getCurrentUserId()
    }

    fun isLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }
}