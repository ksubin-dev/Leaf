package com.subin.leafy.domain.usecase.timer

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TimerSettings
import com.subin.leafy.domain.repository.TimerRepository
import javax.inject.Inject

class UpdateTimerSettingsUseCase @Inject constructor(
    private val timerRepository: TimerRepository
) {
    suspend operator fun invoke(settings: TimerSettings): DataResourceResult<Unit> {
        return timerRepository.updateTimerSettings(settings)
    }
}