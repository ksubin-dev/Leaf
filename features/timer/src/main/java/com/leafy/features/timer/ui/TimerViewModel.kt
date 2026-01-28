package com.leafy.features.timer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.ui.model.BrewingSessionNavArgs
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.InfusionRecord
import com.subin.leafy.domain.model.TimerPreset
import com.subin.leafy.domain.usecase.TimerUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timerUseCases: TimerUseCases
) : ViewModel() {
    private val _internalState = MutableStateFlow(TimerUiState())
    private var timerJob: Job? = null


    val uiState: StateFlow<TimerUiState> = combine(
        _internalState,
        timerUseCases.getPresets.invoke(),
        timerUseCases.getTimerSettings.invoke()
    ) { state, presets, settings ->
        state.copy(
            presets = presets,
            settings = settings
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TimerUiState(isLoading = true)
    )

    private val _sideEffect = Channel<TimerSideEffect>()
    val sideEffect: Flow<TimerSideEffect> = _sideEffect.receiveAsFlow()

    init {
        loadLastUsedRecipe()
    }

    private fun loadLastUsedRecipe() {
        viewModelScope.launch {
            val result = timerUseCases.getLastUsedRecipe()
            if (result is DataResourceResult.Success) {
                result.data?.let { (time, temp) ->
                    updateTarget(time, temp)
                }
            }
        }
    }

    fun updateTarget(time: Int, temp: Int) {
        if (_internalState.value.status == TimerStatus.RUNNING) return

        _internalState.update {
            it.copy(
                status = TimerStatus.IDLE,
                currentTeaName = "나만의 차",
                targetTimeSeconds = time,
                targetTemperature = temp,
                remainingSeconds = time,
                progress = 1.0f,
                selectedPresetId = null,
                infusionRecords = emptyList(),
                isAlarmFired = false
            )
        }
    }

    fun selectPreset(preset: TimerPreset) {
        if (_internalState.value.status == TimerStatus.RUNNING) return
        _internalState.update {
            it.copy(
                status = TimerStatus.IDLE,
                currentTeaName = preset.name,
                selectedPresetId = preset.id,
                selectedTeaType = preset.teaType,
                targetTimeSeconds = preset.recipe.brewTimeSeconds,
                targetTemperature = preset.recipe.waterTemp,
                leafAmount = preset.recipe.leafAmount,
                waterAmount = preset.recipe.waterAmount,
                selectedTeaware = preset.recipe.teaware,
                remainingSeconds = preset.recipe.brewTimeSeconds,
                progress = 1.0f,
                infusionRecords = emptyList(),
                isAlarmFired = false
            )
        }
        sendEffect(TimerSideEffect.ShowSnackbar(UiText.DynamicString("새로운 레시피가 선택되었습니다.")))
    }

    fun markAlarmAsFired() {
        _internalState.update { it.copy(isAlarmFired = true) }
    }

    fun startTimer() {
        if (_internalState.value.status == TimerStatus.RUNNING) return

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            if (_internalState.value.status == TimerStatus.COMPLETED || _internalState.value.remainingSeconds <= 0) {
                _internalState.update { it.copy(
                    remainingSeconds = it.targetTimeSeconds,
                    progress = 1.0f
                ) }
            }

            _internalState.update { it.copy(status = TimerStatus.RUNNING, isAlarmFired = false) }

            while (_internalState.value.remainingSeconds > 0) {
                delay(1000L)
                _internalState.update { state ->
                    val newRemaining = state.remainingSeconds - 1
                    val progress = newRemaining.toFloat() / state.targetTimeSeconds.toFloat()
                    state.copy(remainingSeconds = newRemaining, progress = progress)
                }
            }
            completeTimer()
        }
        saveLastRecord()
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _internalState.update { it.copy(status = TimerStatus.PAUSED) }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _internalState.update { state ->
            state.copy(
                status = TimerStatus.IDLE,
                isAlarmFired = false,
                remainingSeconds = state.targetTimeSeconds,
                progress = 1.0f
            )
        }
    }

    private fun completeTimer() {
        _internalState.update {
            it.copy(
                status = TimerStatus.COMPLETED,
                remainingSeconds = 0,
                progress = 0f
            )
        }
        recordInfusion()
    }

    private fun saveLastRecord() {
        val current = _internalState.value
        viewModelScope.launch {
            timerUseCases.saveLastUsedRecipe(current.targetTimeSeconds, current.targetTemperature)
        }
    }

    fun savePreset(preset: TimerPreset) {
        viewModelScope.launch {
            val result = timerUseCases.savePreset(preset)
            if (result is DataResourceResult.Success) {
                selectPreset(preset)
                sendEffect(TimerSideEffect.ShowSnackbar(UiText.DynamicString("레시피 '${preset.name}' 저장 완료!")))
            } else if (result is DataResourceResult.Failure) {
                val msg = result.exception.message ?: "저장 실패"
                sendEffect(TimerSideEffect.ShowSnackbar(UiText.DynamicString(msg)))
            }
        }
    }

    fun deletePreset(presetId: String) {
        val target = uiState.value.presets.find { it.id == presetId }

        if (target?.isDefault == true) {
            sendEffect(TimerSideEffect.ShowSnackbar(UiText.DynamicString("기본 프리셋은 삭제할 수 없습니다.")))
            return
        }

        viewModelScope.launch {
            val result = timerUseCases.deletePreset(presetId)

            if (result is DataResourceResult.Success) {
                sendEffect(TimerSideEffect.ShowSnackbar(UiText.DynamicString("레시피가 삭제되었습니다.")))

                if (_internalState.value.selectedPresetId == presetId) {
                    _internalState.update { it.copy(selectedPresetId = null, currentTeaName = "나만의 차") }
                }
            } else if (result is DataResourceResult.Failure) {
                val msg = result.exception.message ?: "삭제 실패"
                sendEffect(TimerSideEffect.ShowSnackbar(UiText.DynamicString(msg)))
            }
        }
    }

    fun deleteInfusionRecord(count: Int) {
        _internalState.update { state ->
            val updatedRecords = state.infusionRecords.filterNot { it.count == count }
            state.copy(infusionRecords = updatedRecords)
        }
    }

    fun recordInfusion() {
        _internalState.update { state ->
            if (state.status == TimerStatus.IDLE && state.remainingSeconds == state.targetTimeSeconds) return@update state

            val currentTime = System.currentTimeMillis()
            val lastRecordTimestamp = state.infusionRecords.lastOrNull()?.timestamp ?: 0L
            if (currentTime - lastRecordTimestamp < 2000L) return@update state

            val nextCount = (state.infusionRecords.maxOfOrNull { it.count } ?: 0) + 1
            val actualBrewTime = state.targetTimeSeconds - state.remainingSeconds
            val finalBrewTime = if (actualBrewTime <= 0) state.targetTimeSeconds else actualBrewTime

            val newRecord = InfusionRecord(
                count = nextCount,
                timeSeconds = finalBrewTime,
                waterTemp = state.targetTemperature,
                timestamp = System.currentTimeMillis()
            )

            sendEffect(TimerSideEffect.ShowSnackbar(UiText.DynamicString("${nextCount}번째 우림이 기록되었습니다.")))

            state.copy(infusionRecords = state.infusionRecords + newRecord)
        }
    }

    fun navigateToNote() {
        val state = _internalState.value
        val navArgs = BrewingSessionNavArgs(
            teaName = state.currentTeaName,
            teaType = state.selectedTeaType.name,
            waterTemp = state.targetTemperature,
            leafAmount = state.leafAmount,
            waterAmount = state.waterAmount,
            teaware = state.selectedTeaware.name,
            records = state.infusionRecords.map { it.toInfusionRecordDto() }
        )
        val json = Json.encodeToString(navArgs)
        sendEffect(TimerSideEffect.NavigateToNote(json))
    }

    fun showDefaultPresetWarning() {
        sendEffect(TimerSideEffect.ShowSnackbar(UiText.DynamicString("기본 레시피는 수정할 수 없습니다.")))
    }

    private fun sendEffect(effect: TimerSideEffect) {
        viewModelScope.launch {
            _sideEffect.send(effect)
        }
    }
}