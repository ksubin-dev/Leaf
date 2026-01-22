package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.id.UserId
import com.subin.leafy.domain.repository.UserRepository

class GetUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: UserId): User {
        return userRepository.getUser(userId)
    }
}