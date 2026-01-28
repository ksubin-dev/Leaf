package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(targetUserId: String): DataResourceResult<User> {
        if (targetUserId.isBlank()) {
            return DataResourceResult.Failure(Exception("유효하지 않은 유저 ID입니다."))
        }
        return userRepository.getUserProfile(targetUserId)
    }
}