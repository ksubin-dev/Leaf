package com.subin.leafy.domain.usecase.auth

import com.subin.leafy.domain.repository.AuthRepository

class CheckLoginStatusUseCase(
    private val authRepository: AuthRepository
) {
    // 1. 메인 기능: 내 ID(UID) 가져오기
    operator fun invoke(): String? {
        return authRepository.getCurrentUserId()
    }

    // 2. 보조 기능: 로그인 여부만 빠르게 확인 (True/False)
    fun isLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }
}