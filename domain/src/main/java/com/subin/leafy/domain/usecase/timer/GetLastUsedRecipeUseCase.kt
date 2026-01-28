package com.subin.leafy.domain.usecase.timer

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.TimerRepository
import javax.inject.Inject

class GetLastUsedRecipeUseCase @Inject constructor(
    private val timerRepository: TimerRepository
) {
    suspend operator fun invoke(): DataResourceResult<Pair<Int, Int>?> {
        return timerRepository.getLastUsedRecipe()
    }
}