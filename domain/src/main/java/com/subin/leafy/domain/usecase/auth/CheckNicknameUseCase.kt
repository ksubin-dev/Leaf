package com.subin.leafy.domain.usecase.auth

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.AuthRepository
import javax.inject.Inject

class CheckNicknameUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(nickname: String): DataResourceResult<Boolean> {

        val trimmedNickname = nickname.trim()

        if (trimmedNickname.isBlank()) {
            return DataResourceResult.Failure(Exception("닉네임을 입력해주세요."))
        }

        if (trimmedNickname.length !in 2..10) {
            return DataResourceResult.Failure(Exception("닉네임은 2~10글자 사이여야 합니다."))
        }

        val regex = Regex("^[가-힣a-zA-Z0-9]*$")
        if (!regex.matches(trimmedNickname)) {
            return DataResourceResult.Failure(Exception("특수문자나 공백은 사용할 수 없습니다."))
        }

        return authRepository.checkNicknameAvailability(trimmedNickname)
    }
}