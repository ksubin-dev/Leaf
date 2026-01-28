package com.subin.leafy.domain.usecase.tea

import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.repository.TeaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTeasUseCase @Inject constructor(
    private val teaRepository: TeaRepository
) {
    operator fun invoke(): Flow<List<TeaItem>> {
        return teaRepository.getTeasFlow()
    }
}