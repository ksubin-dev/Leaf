package com.subin.leafy.domain.usecase.auth

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): DataResourceResult<User> {
        if (email.isBlank() || password.isBlank()) {
            return DataResourceResult.Failure(Exception("이메일과 비밀번호를 모두 입력해주세요."))
        }
        return authRepository.login(email, password)
    }
}