package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetFollowingIdsFlowUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(userId: String): Flow<DataResourceResult<List<String>>> {
        if (userId.isBlank()) {
            return flow {
                emit(DataResourceResult.Failure(Exception("유효하지 않은 User ID입니다.")))
            }
        }
        return userRepository.getFollowingIdsFlow(userId)
    }
}