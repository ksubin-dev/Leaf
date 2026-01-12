package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

//앱 어디서든 내 정보를 실시간으로 구독할 때 씁니다. (드로어, 마이페이지)
class GetMyProfileUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<DataResourceResult<User>> {
        return userRepository.getMyProfileFlow()
    }
}