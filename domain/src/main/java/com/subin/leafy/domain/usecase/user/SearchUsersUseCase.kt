package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.repository.UserRepository

class SearchUsersUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(query: String): DataResourceResult<List<User>> {
        if (query.isBlank()) return DataResourceResult.Success(emptyList())
        return userRepository.searchUsers(query)
    }
}