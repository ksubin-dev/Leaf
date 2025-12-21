package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.model.id.UserId
import com.subin.leafy.domain.repository.UserRepository

class GetCurrentUserIdUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): UserId {
        return userRepository.getCurrentUserId()
    }
}