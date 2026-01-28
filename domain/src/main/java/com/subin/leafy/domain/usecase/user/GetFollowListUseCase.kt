package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.repository.UserRepository
import javax.inject.Inject

enum class FollowType { FOLLOWER, FOLLOWING }

class GetFollowListUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, type: FollowType): DataResourceResult<List<User>> {
        if (userId.isBlank()) {
            return DataResourceResult.Failure(Exception("유효하지 않은 유저 ID입니다."))
        }

        return when (type) {
            FollowType.FOLLOWER -> userRepository.getFollowers(userId)
            FollowType.FOLLOWING -> userRepository.getFollowings(userId)
        }
    }
}