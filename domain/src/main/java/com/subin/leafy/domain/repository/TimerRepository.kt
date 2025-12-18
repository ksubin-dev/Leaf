package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TimerPreset
import kotlinx.coroutines.flow.Flow

interface TimerRepository {
    // 저장된 프리셋 목록 가져오기
    fun getPresets(): Flow<DataResourceResult<List<TimerPreset>>>
}