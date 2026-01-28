package com.subin.leafy.domain.usecase.timer

import com.subin.leafy.domain.model.TimerSettings
import com.subin.leafy.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTimerSettingsUseCase @Inject constructor(
    private val timerRepository: TimerRepository
) {
    operator fun invoke(): Flow<TimerSettings> {
        return timerRepository.getTimerSettingsFlow()
    }
}