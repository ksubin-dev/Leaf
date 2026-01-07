package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class UpdateProfileUseCase(
    private val repository: UserRepository
) {
    operator fun invoke(user: User): Flow<DataResourceResult<Unit>> {
        return repository.updateProfile(user)
    }
}