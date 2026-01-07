package com.subin.leafy.data.datasource.remote

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TimerPreset

interface TimerDataSource {
    suspend fun getPresets(): DataResourceResult<List<TimerPreset>>
    suspend fun savePreset(preset: TimerPreset): DataResourceResult<Unit>
    suspend fun deletePreset(presetId: String): DataResourceResult<Unit>
}