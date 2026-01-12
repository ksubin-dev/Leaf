package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.repository.UserRepository

// 게시글 작성자 등을 눌렀을 때 상대방 정보를 가져옵니다.
class GetUserProfileUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(targetUserId: String): DataResourceResult<User> {
        if (targetUserId.isBlank()) {
            return DataResourceResult.Failure(Exception("유효하지 않은 유저 ID입니다."))
        }
        return userRepository.getUserProfile(targetUserId)
    }
}