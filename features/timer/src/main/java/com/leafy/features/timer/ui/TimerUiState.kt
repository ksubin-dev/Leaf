package com.leafy.features.timer.ui

import com.subin.leafy.domain.model.InfusionRecord
import com.subin.leafy.domain.model.TimerPreset

data class TimerUiState(
    val isLoading: Boolean = false,
    val selectedPreset: TimerPreset = TimerPreset(name = "Loading...", baseTimeSeconds = 0),
    val timeLeft: Int = 0,
    val initialTime: Int = 0,
    val isRunning: Boolean = false,
    val currentInfusion: Int = 1,
    val records: List<InfusionRecord> = emptyList(),
    val presets: List<TimerPreset> = emptyList(),
    val errorMessage: String? = null
) {

    val progress: Float
        get() = if (initialTime > 0) timeLeft.toFloat() / initialTime else 0f

    val formattedTime: String
        get() {
            val m = timeLeft / 60
            val s = timeLeft % 60
            return "%02d:%02d".format(m, s)
        }
}