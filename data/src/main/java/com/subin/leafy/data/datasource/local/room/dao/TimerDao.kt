package com.subin.leafy.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.subin.leafy.data.datasource.local.room.entity.TimerPresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDao {

    // 1. 전체 프리셋 가져오기 (실시간)
    @Query("SELECT * FROM timer_presets")
    fun getAllPresets(): Flow<List<TimerPresetEntity>>

    // 2. 프리셋 저장 & 수정 (ID 같으면 덮어쓰기)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: TimerPresetEntity)

    // 3. 여러 개 저장 (기본 프리셋 넣을 때 사용)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresets(presets: List<TimerPresetEntity>)

    // 4. 삭제
    @Query("DELETE FROM timer_presets WHERE id = :presetId")
    suspend fun deletePreset(presetId: String)

    // 5. 기본 프리셋인지 확인용 (삭제 방지 로직 등에 활용 가능)
    @Query("SELECT count(*) FROM timer_presets")
    suspend fun getPresetCount(): Int
}