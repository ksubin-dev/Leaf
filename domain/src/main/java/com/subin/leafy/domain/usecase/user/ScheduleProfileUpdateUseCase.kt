package com.subin.leafy.domain.usecase.user

import com.subin.leafy.domain.repository.UserRepository
import javax.inject.Inject

class ScheduleProfileUpdateUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(nickname: String, bio: String, imageUriString: String?) {
        repository.scheduleProfileUpdate(nickname, bio, imageUriString)
    }
}