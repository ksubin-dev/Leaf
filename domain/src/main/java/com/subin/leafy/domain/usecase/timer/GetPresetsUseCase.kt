package com.subin.leafy.domain.usecase.timer

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TimerPreset
import com.subin.leafy.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow

class GetPresetsUseCase(private val repository: TimerRepository) {
    operator fun invoke(): Flow<DataResourceResult<List<TimerPreset>>> = repository.getPresets()
}