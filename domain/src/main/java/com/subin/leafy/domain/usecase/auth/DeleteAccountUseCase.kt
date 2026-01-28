package com.subin.leafy.domain.usecase.auth

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.AuthRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): DataResourceResult<Unit> {
        return authRepository.deleteAccount()
    }
}