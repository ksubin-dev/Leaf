package com.subin.leafy.data.datasource

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TimerPreset

interface TimerDataSource {
    suspend fun getPresets(): DataResourceResult<List<TimerPreset>>
}