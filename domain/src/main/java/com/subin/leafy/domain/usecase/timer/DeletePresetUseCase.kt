package com.subin.leafy.domain.usecase.timer

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.TimerRepository

class DeletePresetUseCase(
    private val timerRepository: TimerRepository
) {
    suspend operator fun invoke(presetId: String): DataResourceResult<Unit> {
        if (presetId.isBlank()) {
            return DataResourceResult.Failure(Exception("삭제할 프리셋을 찾을 수 없습니다."))
        }
        return timerRepository.deletePreset(presetId)
    }
}