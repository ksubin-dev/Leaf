package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.repository.UserRepository

class GetCurrentUserIdUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): String? {
        return userRepository.getCurrentUserId()
    }
}