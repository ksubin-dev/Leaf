package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.AuthRepository

class GetCurrentUserIdUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): DataResourceResult<String> {
        val userId = authRepository.getCurrentUserId()
        return if (userId != null) {
            DataResourceResult.Success(userId)
        } else {
            DataResourceResult.Failure(Exception("로그인된 사용자가 없습니다."))
        }
    }
}