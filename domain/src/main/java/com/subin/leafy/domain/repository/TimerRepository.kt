package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TimerPreset
import com.subin.leafy.domain.model.TimerSettings
import kotlinx.coroutines.flow.Flow

interface TimerRepository {

    // --- 1. 프리셋 (Presets) ---
    fun getPresetsFlow(): Flow<List<TimerPreset>>


    suspend fun savePreset(preset: TimerPreset): DataResourceResult<Unit>

    suspend fun deletePreset(presetId: String): DataResourceResult<Unit>


    // --- 2. 최근 사용 기록 ---
    suspend fun saveLastUsedRecipe(timeSeconds: Int, temperature: Int): DataResourceResult<Unit>

    suspend fun getLastUsedRecipe(): DataResourceResult<Pair<Int, Int>?>


    // --- 3. 환경설정 ---
    fun getTimerSettingsFlow(): Flow<TimerSettings>

    suspend fun updateTimerSettings(settings: TimerSettings): DataResourceResult<Unit>
}