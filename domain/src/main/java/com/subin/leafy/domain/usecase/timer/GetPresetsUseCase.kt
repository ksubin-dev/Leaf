package com.subin.leafy.domain.usecase.timer

import com.subin.leafy.domain.model.TimerPreset
import com.subin.leafy.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow

class GetPresetsUseCase(
    private val timerRepository: TimerRepository
) {
    operator fun invoke(): Flow<List<TimerPreset>> {
        return timerRepository.getPresetsFlow()
    }
}