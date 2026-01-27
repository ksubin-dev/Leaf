package com.subin.leafy.domain.usecase.tea

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.TeaRepository
import javax.inject.Inject

class SyncTeasUseCase @Inject constructor(
    private val teaRepository: TeaRepository
) {
    suspend operator fun invoke(): DataResourceResult<Unit> {
        return teaRepository.syncTeas()
    }
}