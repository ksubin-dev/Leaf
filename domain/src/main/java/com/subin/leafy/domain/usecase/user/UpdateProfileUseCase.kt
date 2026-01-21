package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.UserRepository

class UpdateProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        nickname: String? = null,
        bio: String? = null,
        profileUrl: String? = null
    ): DataResourceResult<Unit> {

        if (bio != null && bio.length > 50) {
            return DataResourceResult.Failure(Exception("한줄 소개는 50자 이내로 작성해주세요."))
        }

        val finalNickname = if (nickname != null) {
            val trimmed = nickname.trim()

            if (trimmed.length !in 2..10) {
                return DataResourceResult.Failure(Exception("닉네임은 2~10글자 사이여야 합니다."))
            }

            val regex = Regex("^[가-힣a-zA-Z0-9]*$")
            if (!regex.matches(trimmed)) {
                return DataResourceResult.Failure(Exception("특수문자나 공백은 사용할 수 없습니다."))
            }

            when (val checkResult = userRepository.checkNicknameAvailability(trimmed)) {
                is DataResourceResult.Success -> {
                    if (!checkResult.data) {
                        return DataResourceResult.Failure(Exception("이미 사용 중인 닉네임입니다."))
                    }
                }
                is DataResourceResult.Failure -> {
                    return DataResourceResult.Failure(checkResult.exception)
                }
                else -> {}
            }
            trimmed
        } else {
            null
        }

        return userRepository.updateProfile(
            nickname = finalNickname,
            bio = bio,
            profileUrl = profileUrl
        )
    }
}