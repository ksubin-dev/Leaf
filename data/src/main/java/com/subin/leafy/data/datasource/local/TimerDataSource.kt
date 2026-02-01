package com.subin.leafy.data.datasource.local

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TimerPreset
import com.subin.leafy.domain.model.TimerSettings
import kotlinx.coroutines.flow.Flow

interface TimerDataSource {

    fun getPresets(): Flow<List<TimerPreset>>

    suspend fun savePreset(preset: TimerPreset): DataResourceResult<Unit>

    suspend fun deletePreset(presetId: String): DataResourceResult<Unit>

    suspend fun saveLastUsedRecipe(name: String, timeSeconds: Int, temperature: Int): DataResourceResult<Unit>

    suspend fun getLastUsedRecipe(): DataResourceResult<Triple<String, Int, Int>?>

    fun getTimerSettings(): Flow<TimerSettings>

    suspend fun setTimerSettings(settings: TimerSettings): DataResourceResult<Unit>

    suspend fun checkAndInitDefaultPresets(defaultPresets: List<TimerPreset>): DataResourceResult<Unit>
}