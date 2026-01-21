package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.local.TimerDataSource
import com.subin.leafy.data.datasource.local.assets.DefaultTimerPresets
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TimerPreset
import com.subin.leafy.domain.model.TimerSettings
import com.subin.leafy.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart

class TimerRepositoryImpl(
    private val timerDataSource: TimerDataSource
) : TimerRepository {


    override fun getPresetsFlow(): Flow<List<TimerPreset>> {
        return timerDataSource.getPresets()
            .onStart {
                timerDataSource.checkAndInitDefaultPresets(DefaultTimerPresets.list)
            }
    }

    override suspend fun savePreset(preset: TimerPreset): DataResourceResult<Unit> {
        return timerDataSource.savePreset(preset)
    }

    override suspend fun deletePreset(presetId: String): DataResourceResult<Unit> {
        return timerDataSource.deletePreset(presetId)
    }



    override suspend fun saveLastUsedRecipe(timeSeconds: Int, temperature: Int): DataResourceResult<Unit> {
        return timerDataSource.saveLastUsedRecipe(timeSeconds, temperature)
    }

    override suspend fun getLastUsedRecipe(): DataResourceResult<Pair<Int, Int>?> {
        return timerDataSource.getLastUsedRecipe()
    }



    override fun getTimerSettingsFlow(): Flow<TimerSettings> {
        return timerDataSource.getTimerSettings()
    }

    override suspend fun updateTimerSettings(settings: TimerSettings): DataResourceResult<Unit> {
        return timerDataSource.setTimerSettings(settings)
    }
}