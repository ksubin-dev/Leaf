package com.leafy.features.timer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.InfusionRecord
import com.subin.leafy.domain.model.TimerPreset
import com.subin.leafy.domain.usecase.timer.TimerUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class TimerViewModel(
    private val timerUseCases: TimerUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadPresets()
    }

    private fun loadPresets() {
        viewModelScope.launch {
            timerUseCases.getPresets().collectLatest { result ->
                when (result) {
                    is DataResourceResult.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is DataResourceResult.Success -> {
                        val firstPreset = result.data.firstOrNull() ?: TimerPreset()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                presets = result.data,
                                selectedPreset = firstPreset,
                                timeLeft = firstPreset.baseTimeSeconds,
                                initialTime = firstPreset.baseTimeSeconds
                            )
                        }
                    }
                    is DataResourceResult.Failure -> {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = result.exception.message)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun toggleTimer() {
        if (_uiState.value.isRunning) pauseTimer() else startTimer()
    }

    private fun startTimer() {
        if (timerJob?.isActive == true) return
        _uiState.update { it.copy(isRunning = true) }
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeLeft > 0) {
                delay(1000L)
                _uiState.update { it.copy(timeLeft = it.timeLeft - 1) }
            }
            recordInfusion()
        }
    }

    private fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
    }

    fun resetTimer() {
        pauseTimer()
        _uiState.update { it.copy(timeLeft = it.initialTime) }
    }

    fun recordInfusion() {
        pauseTimer()

        val current = _uiState.value
        val elapsed = current.initialTime - current.timeLeft
        val finalTime = if (elapsed <= 0) current.initialTime else elapsed

        val record = InfusionRecord(
            count = current.currentInfusion,
            timeSeconds = finalTime,
            formattedTime = "%02d:%02d".format(finalTime / 60, finalTime % 60)
        )

        _uiState.update {
            it.copy(
                records = listOf(record) + it.records,
                currentInfusion = it.currentInfusion + 1,
                timeLeft = it.initialTime,
                isRunning = false
            )
        }
    }

    fun getRecordsAsJson(): String {
        // [1, 2, 3] 형태의 리스트를 "[{"count":1, "time":60}, ...]" 형태의 문자열로 변환
        return Json.encodeToString(_uiState.value.records)
    }
}