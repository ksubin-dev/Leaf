package com.subin.leafy.domain.usecase.tea

import com.subin.leafy.domain.repository.TeaRepository
import kotlinx.coroutines.flow.Flow

class GetTeaCountUseCase(
    private val teaRepository: TeaRepository
) {
    operator fun invoke(): Flow<Int> {
        return teaRepository.getTeaCountFlow()
    }
}