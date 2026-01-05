package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.user.GetCurrentUserIdUseCase
import com.subin.leafy.domain.usecase.user.GetUserStatsUseCase
import com.subin.leafy.domain.usecase.user.GetUserUseCase
import com.subin.leafy.domain.usecase.user.UpdateProfileUseCase

data class UserUseCases(
    val getCurrentUserId: GetCurrentUserIdUseCase,
    val getUser: GetUserUseCase,
    val getUserStats: GetUserStatsUseCase,
    val updateProfile: UpdateProfileUseCase
)