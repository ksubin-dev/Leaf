package com.leafy.features.timer.ui
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.subin.leafy.domain.common.DataResourceResult
//import com.subin.leafy.domain.model.InfusionRecord
//import com.subin.leafy.domain.model.TimerPreset
//import com.subin.leafy.domain.usecase.TimerUseCases
//import kotlinx.coroutines.Job
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import kotlinx.serialization.json.Json
//
//class TimerViewModel(
//    private val timerUseCases: TimerUseCases
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(TimerUiState())
//    val uiState = _uiState.asStateFlow()
//
//    private var timerJob: Job? = null
//
//    init {
//        loadPresets()
//    }
//
//    /**
//     * Repository(Mock)로부터 프리셋 목록을 가져와 초기 상태를 설정합니다.
//     */
//    private fun loadPresets() {
//        viewModelScope.launch {
//            timerUseCases.getPresets().collectLatest { result ->
//                when (result) {
//                    is DataResourceResult.Loading -> {
//                        _uiState.update { it.copy(isLoading = true) }
//                    }
//                    is DataResourceResult.Success -> {
//                        val firstPreset = result.data.firstOrNull() ?: TimerPreset()
//                        _uiState.update {
//                            it.copy(
//                                isLoading = false,
//                                presets = result.data,
//                                selectedPreset = firstPreset,
//                                timeLeft = firstPreset.baseTimeSeconds,
//                                initialTime = firstPreset.baseTimeSeconds
//                            )
//                        }
//                    }
//                    is DataResourceResult.Failure -> {
//                        _uiState.update {
//                            it.copy(isLoading = false, errorMessage = result.exception.message)
//                        }
//                    }
//                    else -> {}
//                }
//            }
//        }
//    }
//
//    // --- 바텀시트 제어 로직 ---
//
//    fun openPresetSheet() {
//        _uiState.update { it.copy(isBottomSheetOpen = true) }
//    }
//
//    fun closePresetSheet() {
//        _uiState.update { it.copy(isBottomSheetOpen = false) }
//    }
//
//    /**
//     * 사용자가 리스트에서 프리셋을 선택했을 때 호출됩니다.
//     */
//    fun selectPreset(preset: TimerPreset) {
//        pauseTimer() // 새로운 시간 설정 시 타이머 정지
//        _uiState.update {
//            it.copy(
//                selectedPreset = preset,
//                timeLeft = preset.baseTimeSeconds,
//                initialTime = preset.baseTimeSeconds,
//                isBottomSheetOpen = false // 선택 후 시트 닫기
//            )
//        }
//    }
//
//    // --- 타이머 핵심 로직 ---
//
//    fun toggleTimer() {
//        if (_uiState.value.isRunning) pauseTimer() else startTimer()
//    }
//
//    private fun startTimer() {
//        if (timerJob?.isActive == true) return
//        _uiState.update { it.copy(isRunning = true) }
//        timerJob = viewModelScope.launch {
//            while (_uiState.value.timeLeft > 0) {
//                delay(1000L)
//                _uiState.update { it.copy(timeLeft = it.timeLeft - 1) }
//            }
//            recordInfusion()
//        }
//    }
//
//    fun pauseTimer() {
//        timerJob?.cancel()
//        _uiState.update { it.copy(isRunning = false) }
//    }
//
//    fun resetTimer() {
//        pauseTimer()
//        _uiState.update { it.copy(timeLeft = it.initialTime) }
//    }
//
//    /**
//     * 현재 우림 세션을 기록합니다.
//     */
//    fun recordInfusion() {
//        pauseTimer()
//
//        val current = _uiState.value
//        val elapsed = current.initialTime - current.timeLeft
//        val finalTime = if (elapsed <= 0) current.initialTime else elapsed
//
//        val record = InfusionRecord(
//            count = current.currentInfusion,
//            timeSeconds = finalTime,
//            formattedTime = "%02d:%02d".format(finalTime / 60, finalTime % 60)
//        )
//
//        _uiState.update {
//            it.copy(
//                records = listOf(record) + it.records,
//                currentInfusion = it.currentInfusion + 1,
//                timeLeft = it.initialTime,
//                isRunning = false
//            )
//        }
//    }
//
//    /**
//     * 기록된 데이터를 JSON 문자열로 변환하여 Note 화면으로 전달할 준비를 합니다.
//     */
//    fun getRecordsAsJson(): String {
//        return Json.encodeToString(_uiState.value.records)
//    }
//}