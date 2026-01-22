package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.UserRepository

class UpdateFcmTokenUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(isEnabled: Boolean): DataResourceResult<Unit> {
        return userRepository.syncFcmToken(isEnabled)
    }
}