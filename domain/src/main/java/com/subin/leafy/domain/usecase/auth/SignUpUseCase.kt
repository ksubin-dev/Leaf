package com.subin.leafy.domain.usecase.auth

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.repository.AuthRepository

class SignUpUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        nickname: String,
        profileImageUri: String? = null
    ): DataResourceResult<User> {

        // 1. 비즈니스 로직 (유효성 검사)
        if (email.isBlank() || !email.contains("@")) {
            return DataResourceResult.Failure(Exception("올바른 이메일 형식을 입력해주세요."))
        }
        if (password.length < 6) {
            return DataResourceResult.Failure(Exception("비밀번호는 최소 6자 이상이어야 합니다."))
        }
        if (nickname.isBlank()) {
            return DataResourceResult.Failure(Exception("닉네임을 입력해주세요."))
        }
        // 2. 검사 통과 시 레포지토리 호출
        return authRepository.signUp(
            email = email,
            password = password,
            nickname = nickname,
            profileImageUri = profileImageUri
        )
    }
}