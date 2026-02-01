package com.subin.leafy.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.subin.leafy.data.datasource.local.room.entity.TimerPresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDao {

    @Query("SELECT * FROM timer_presets ORDER BY isDefault DESC, name ASC")
    fun getAllPresets(): Flow<List<TimerPresetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: TimerPresetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresets(presets: List<TimerPresetEntity>)

    @Query("DELETE FROM timer_presets WHERE id = :presetId")
    suspend fun deletePreset(presetId: String)

    @Query("SELECT count(*) FROM timer_presets")
    suspend fun getPresetCount(): Int
}