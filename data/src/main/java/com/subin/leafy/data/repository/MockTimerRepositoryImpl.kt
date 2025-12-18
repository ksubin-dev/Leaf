package com.subin.leafy.data.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TimerPreset
import com.subin.leafy.domain.repository.TimerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MockTimerRepositoryImpl : TimerRepository {

    private val defaultPresets = listOf(
        TimerPreset(1, "Daily Green Tea", "80°C", 120, "2g", "Green"),
        TimerPreset(2, "Morning Oolong", "95°C", 45, "5g", "Oolong"),
        TimerPreset(3, "Deep Pu-erh", "100°C", 20, "7g", "Black"),
        TimerPreset(4, "Earl Grey", "90°C", 180, "3g", "Black")
    )

    override fun getPresets(): Flow<DataResourceResult<List<TimerPreset>>> = flow {
        emit(DataResourceResult.Loading)
        // delay(500) // 필요 시 지연
        emit(DataResourceResult.Success(defaultPresets))
    }.flowOn(Dispatchers.IO)
}