package com.subin.leafy.domain.usecase.timer

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.TimerRepository
import javax.inject.Inject

class SaveLastUsedRecipeUseCase @Inject constructor(
    private val timerRepository: TimerRepository
) {
    suspend operator fun invoke(timeSeconds: Int, temperature: Int): DataResourceResult<Unit> {

        if (timeSeconds <= 0) {
            return DataResourceResult.Failure(Exception("유효하지 않은 시간입니다."))
        }
        if (temperature !in 0..100) {
            return DataResourceResult.Failure(Exception("유효하지 않은 온도입니다."))
        }

        return timerRepository.saveLastUsedRecipe(timeSeconds, temperature)
    }
}