package com.leafy.features.timer.ui

import com.subin.leafy.domain.model.InfusionRecord
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.model.TeawareType
import com.subin.leafy.domain.model.TimerPreset
import com.subin.leafy.domain.model.TimerSettings

enum class TimerStatus {
    IDLE,       // 대기 (설정 가능)
    RUNNING,    // 카운트다운 중
    PAUSED,     // 일시 정지
    COMPLETED   // 완료 (알람 울림)
}

data class TimerUiState(
    val isLoading: Boolean = false,

    val presets: List<TimerPreset> = emptyList(),
    val settings: TimerSettings = TimerSettings(),

    val currentTeaName: String = "나만의 차",
    val targetTimeSeconds: Int = 180,
    val targetTemperature: Int = 90,
    val leafAmount: Float = 3f,
    val waterAmount: Int = 150,

    val selectedTeaType: TeaType = TeaType.GREEN,
    val selectedPresetId: String? = null,
    val selectedTeaware: TeawareType = TeawareType.MUG,

    val status: TimerStatus = TimerStatus.IDLE,
    val remainingSeconds: Int = 180,
    val progress: Float = 1.0f,

    val infusionRecords: List<InfusionRecord> = emptyList(),
    val isAlarmFired: Boolean = false,
    val userMessage: String? = null
)