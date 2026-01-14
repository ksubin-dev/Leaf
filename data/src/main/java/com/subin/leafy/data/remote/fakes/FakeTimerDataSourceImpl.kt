package com.subin.leafy.data.remote.fakes

import com.subin.leafy.data.datasource.TimerDataSource
import com.subin.leafy.data.mapper.toDomainList
import com.subin.leafy.data.model.dto.TimerPresetDTO
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TimerPreset

class FakeTimerDataSourceImpl : TimerDataSource {

    private val defaultPresets = listOf(
        TimerPresetDTO(1, "Daily Green Tea", "80°C", 120, "2g", "Green"),
        TimerPresetDTO(2, "Morning Oolong", "95°C", 45, "5g", "Oolong"),
        TimerPresetDTO(3, "Deep Pu-erh", "100°C", 20, "7g", "Black"),
        TimerPresetDTO(4, "Earl Grey", "90°C", 180, "3g", "Black")
    )

    override suspend fun getPresets(): DataResourceResult<List<TimerPreset>> = runCatching {
        // DTO 리스트를 Domain 리스트로 변환하여 전달
        DataResourceResult.Success(defaultPresets.toDomainList())
    }.getOrElse {
        DataResourceResult.Failure(it)
    }
}