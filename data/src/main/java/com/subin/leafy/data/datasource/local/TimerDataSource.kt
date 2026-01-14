package com.subin.leafy.data.datasource.local

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TimerPreset
import com.subin.leafy.domain.model.TimerSettings
import kotlinx.coroutines.flow.Flow

interface TimerDataSource {

    // --- 1. 프리셋 관리 (Presets) ---
    fun getPresets(): Flow<List<TimerPreset>>

    suspend fun savePreset(preset: TimerPreset): DataResourceResult<Unit>

    suspend fun deletePreset(presetId: String): DataResourceResult<Unit>


    // --- 2. 최근 사용 기록 (Recent) ---
    suspend fun saveLastUsedRecipe(timeSeconds: Int, temperature: Int): DataResourceResult<Unit>

    suspend fun getLastUsedRecipe(): DataResourceResult<Pair<Int, Int>?>


    // --- 3. 타이머 환경설정 (Config) ---
    fun getTimerSettings(): Flow<TimerSettings>

    suspend fun setTimerSettings(settings: TimerSettings): DataResourceResult<Unit>

    // 초기화
    suspend fun checkAndInitDefaultPresets(defaultPresets: List<TimerPreset>): DataResourceResult<Unit>
}