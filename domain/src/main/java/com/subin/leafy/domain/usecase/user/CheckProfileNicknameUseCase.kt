package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.UserRepository
import javax.inject.Inject

class CheckProfileNicknameUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(nickname: String): DataResourceResult<Boolean> {

        val trimmed = nickname.trim()
        if (trimmed.isBlank()) {
            return DataResourceResult.Failure(Exception("닉네임을 입력해주세요."))
        }
        if (trimmed.length !in 2..10) {
            return DataResourceResult.Failure(Exception("닉네임은 2~10글자 사이여야 합니다."))
        }
        val regex = Regex("^[가-힣a-zA-Z0-9]*$")
        if (!regex.matches(trimmed)) {
            return DataResourceResult.Failure(Exception("특수문자나 공백은 사용할 수 없습니다."))
        }
        return userRepository.checkNicknameAvailability(trimmed)
    }
}