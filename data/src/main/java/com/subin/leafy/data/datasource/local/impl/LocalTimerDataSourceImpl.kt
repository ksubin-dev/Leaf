package com.subin.leafy.data.datasource.local.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.subin.leafy.data.datasource.local.TimerDataSource
import com.subin.leafy.data.datasource.local.room.dao.TimerDao
import com.subin.leafy.data.mapper.toTimerDomainListFromEntity
import com.subin.leafy.data.mapper.toTimerPresetEntity
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TimerPreset
import com.subin.leafy.domain.model.TimerSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class LocalTimerDataSourceImpl @Inject constructor(
    private val timerDao: TimerDao,
    @Named("timer") private val dataStore: DataStore<Preferences>
) : TimerDataSource {

    companion object {
        private val KEY_LAST_NAME = stringPreferencesKey("last_timer_name")

        private val KEY_LAST_TIME = intPreferencesKey("last_timer_time")
        private val KEY_LAST_TEMP = intPreferencesKey("last_timer_temp")

        private val KEY_VIB_ON = booleanPreferencesKey("conf_vib_on")
        private val KEY_SOUND_ON = booleanPreferencesKey("conf_sound_on")
        private val KEY_SCREEN_ON = booleanPreferencesKey("conf_screen_on")
        private val KEY_SOUND_FILE = stringPreferencesKey("conf_sound_file")
    }


    override fun getPresets(): Flow<List<TimerPreset>> {
        return timerDao.getAllPresets().map { it.toTimerDomainListFromEntity() }
    }

    override suspend fun savePreset(preset: TimerPreset): DataResourceResult<Unit> {
        return try {
            timerDao.insertPreset(preset.toTimerPresetEntity())
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun deletePreset(presetId: String): DataResourceResult<Unit> {
        return try {
            timerDao.deletePreset(presetId)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun checkAndInitDefaultPresets(defaultPresets: List<TimerPreset>): DataResourceResult<Unit> {
        return try {
            if (timerDao.getPresetCount() == 0) {
                timerDao.insertPresets(defaultPresets.map { it.toTimerPresetEntity() })
            }
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun saveLastUsedRecipe(name: String, timeSeconds: Int, temperature: Int): DataResourceResult<Unit> {
        return try {
            dataStore.edit { prefs ->
                prefs[KEY_LAST_NAME] = name
                prefs[KEY_LAST_TIME] = timeSeconds
                prefs[KEY_LAST_TEMP] = temperature
            }
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun getLastUsedRecipe(): DataResourceResult<Triple<String, Int, Int>?> {
        return try {
            val prefs = dataStore.data.firstOrNull()

            if (prefs == null) {
                DataResourceResult.Success(null)
            } else {
                val name = prefs[KEY_LAST_NAME] ?: "나만의 차"
                val time = prefs[KEY_LAST_TIME]
                val temp = prefs[KEY_LAST_TEMP]

                if (time != null && temp != null) {
                    DataResourceResult.Success(Triple(name, time, temp))
                } else {
                    DataResourceResult.Success(null)
                }
            }
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }


    override fun getTimerSettings(): Flow<TimerSettings> {
        return dataStore.data.map { prefs ->
            TimerSettings(
                isVibrationOn = prefs[KEY_VIB_ON] ?: true,
                isSoundOn = prefs[KEY_SOUND_ON] ?: true,
                keepScreenOn = prefs[KEY_SCREEN_ON] ?: true,
                soundFileName = prefs[KEY_SOUND_FILE] ?: "bell_1"
            )
        }
    }

    override suspend fun setTimerSettings(settings: TimerSettings): DataResourceResult<Unit> {
        return try {
            dataStore.edit { prefs ->
                prefs[KEY_VIB_ON] = settings.isVibrationOn
                prefs[KEY_SOUND_ON] = settings.isSoundOn
                prefs[KEY_SCREEN_ON] = settings.keepScreenOn
                prefs[KEY_SOUND_FILE] = settings.soundFileName
            }
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }
}