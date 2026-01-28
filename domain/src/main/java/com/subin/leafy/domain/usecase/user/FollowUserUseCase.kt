package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.UserRepository
import javax.inject.Inject

class FollowUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(targetUserId: String, isFollow: Boolean): DataResourceResult<Unit> {
        if (targetUserId.isBlank()) {
            return DataResourceResult.Failure(Exception("유효하지 않은 유저 ID입니다."))
        }
        return if (isFollow) {
            userRepository.followUser(targetUserId)
        } else {
            userRepository.unfollowUser(targetUserId)
        }
    }
}