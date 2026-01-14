package com.subin.leafy.domain.usecase.tea

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.TeaRepository

class SyncTeasUseCase(
    private val teaRepository: TeaRepository
) {
    suspend operator fun invoke(): DataResourceResult<Unit> {
        return teaRepository.syncTeas()
    }
}