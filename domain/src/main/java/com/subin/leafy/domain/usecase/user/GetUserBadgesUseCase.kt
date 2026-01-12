package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.UserBadge
import com.subin.leafy.domain.repository.UserRepository

class GetUserBadgesUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): DataResourceResult<List<UserBadge>> {

        if (userId.isBlank()) return DataResourceResult.Failure(Exception("유저 ID가 없습니다."))

        return userRepository.getUserBadges(userId)
    }
}