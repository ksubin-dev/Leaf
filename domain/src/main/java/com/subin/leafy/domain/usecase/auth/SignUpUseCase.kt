package com.subin.leafy.domain.usecase.auth

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.AuthUser
import com.subin.leafy.domain.repository.AuthRepository

class SignUpUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
        username: String,
        profileImageUri: String? = null
    ): DataResourceResult<AuthUser> {

        if (!email.contains("@") || !email.contains(".")) {
            return DataResourceResult.Failure(Exception("올바른 이메일 형식이 아닙니다."))
        }

        if (password.length < 6) {
            return DataResourceResult.Failure(Exception("비밀번호는 최소 6자 이상이어야 합니다."))
        }

        if (username.isBlank()) {
            return DataResourceResult.Failure(Exception("사용자 이름을 입력해 주세요."))
        }

        return repo.signUp(email, password, username, profileImageUri)
    }
}