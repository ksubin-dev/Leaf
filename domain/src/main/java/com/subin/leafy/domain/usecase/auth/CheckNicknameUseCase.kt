package com.subin.leafy.domain.usecase.auth

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.AuthRepository

class CheckNicknameUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(nickname: String): DataResourceResult<Boolean> {

        // 1. 공백 제거
        val trimmedNickname = nickname.trim()

        // 2. 빈 값 체크
        if (trimmedNickname.isBlank()) {
            return DataResourceResult.Failure(Exception("닉네임을 입력해주세요."))
        }

        // 3. 길이 체크 (2~10자)
        if (trimmedNickname.length !in 2..10) {
            return DataResourceResult.Failure(Exception("닉네임은 2~10글자 사이여야 합니다."))
        }

        // 4. 특수문자 체크
        val regex = Regex("^[가-힣a-zA-Z0-9]*$")
        if (!regex.matches(trimmedNickname)) {
            return DataResourceResult.Failure(Exception("특수문자나 공백은 사용할 수 없습니다."))
        }

        // 5. 서버 확인
        return authRepository.checkNicknameAvailability(trimmedNickname)
    }
}