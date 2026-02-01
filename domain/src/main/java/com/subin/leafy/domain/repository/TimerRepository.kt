package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TimerPreset
import com.subin.leafy.domain.model.TimerSettings
import kotlinx.coroutines.flow.Flow

interface TimerRepository {

    fun getPresetsFlow(): Flow<List<TimerPreset>>


    suspend fun savePreset(preset: TimerPreset): DataResourceResult<Unit>

    suspend fun deletePreset(presetId: String): DataResourceResult<Unit>

    suspend fun saveLastUsedRecipe(name: String, timeSeconds: Int, temperature: Int): DataResourceResult<Unit>
    suspend fun getLastUsedRecipe(): DataResourceResult<Triple<String, Int, Int>?>

    fun getTimerSettingsFlow(): Flow<TimerSettings>

    suspend fun updateTimerSettings(settings: TimerSettings): DataResourceResult<Unit>
}