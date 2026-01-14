package com.subin.leafy.domain.usecase.timer

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TimerPreset
import com.subin.leafy.domain.repository.TimerRepository

class SavePresetUseCase(
    private val timerRepository: TimerRepository
) {
    suspend operator fun invoke(preset: TimerPreset): DataResourceResult<Unit> {

        // 1. 이름 검사
        val trimmedName = preset.name.trim()
        if (trimmedName.isBlank()) {
            return DataResourceResult.Failure(Exception("프리셋 이름을 입력해주세요."))
        }
        if (trimmedName.length > 20) {
            return DataResourceResult.Failure(Exception("이름은 20자 이내로 설정해주세요."))
        }

        // 2. 시간 검사
        if (preset.recipe.brewTimeSeconds <= 0) {
            return DataResourceResult.Failure(Exception("시간은 0초보다 커야 합니다."))
        }

        // 3. 온도 검사
        if (preset.recipe.waterTemp !in 0..100) {
            return DataResourceResult.Failure(Exception("온도는 0~100℃ 사이여야 합니다."))
        }

        // 4. 정제된 이름으로 저장
        val validPreset = preset.copy(name = trimmedName)

        return timerRepository.savePreset(validPreset)
    }
}