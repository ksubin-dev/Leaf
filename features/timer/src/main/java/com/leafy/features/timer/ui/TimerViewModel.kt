package com.leafy.features.timer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.R
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

sealed interface TimerSideEffect {
    data class ShowToast(val message: UiText) : TimerSideEffect
    data class NavigateToNote(val navArgsJson: String) : TimerSideEffect
    data object NavigateBack : TimerSideEffect
}

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
                result.data?.let { (name, time, temp) ->
                    updateTarget(name, time, temp)
                }
            }
        }
    }

    fun updateTarget(name: String = "나만의 차", time: Int, temp: Int) {
        if (_internalState.value.status == TimerStatus.RUNNING) return

        _internalState.update {
            it.copy(
                status = TimerStatus.IDLE,
                currentTeaName = name,
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
        sendEffect(TimerSideEffect.ShowToast(UiText.StringResource(R.string.msg_preset_selected)))
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

            saveLastRecord()

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
            timerUseCases.saveLastUsedRecipe(
                current.currentTeaName,
                current.targetTimeSeconds,
                current.targetTemperature
            )
        }
    }

    fun savePreset(preset: TimerPreset) {
        viewModelScope.launch {
            val result = timerUseCases.savePreset(preset)
            if (result is DataResourceResult.Success) {
                selectPreset(preset)
                sendEffect(TimerSideEffect.ShowToast(
                    UiText.StringResource(R.string.msg_preset_saved, preset.name)
                ))
            } else if (result is DataResourceResult.Failure) {
                sendEffect(TimerSideEffect.ShowToast(
                    UiText.StringResource(R.string.msg_preset_save_fail)
                ))
            }
        }
    }

    fun deletePreset(presetId: String) {
        val target = uiState.value.presets.find { it.id == presetId }

        if (target?.isDefault == true) {
            sendEffect(TimerSideEffect.ShowToast(
                UiText.StringResource(R.string.msg_default_preset_warning)
            ))
            return
        }

        viewModelScope.launch {
            val result = timerUseCases.deletePreset(presetId)

            if (result is DataResourceResult.Success) {
                sendEffect(TimerSideEffect.ShowToast(
                    UiText.StringResource(R.string.msg_preset_deleted)
                ))

                if (_internalState.value.selectedPresetId == presetId) {
                    _internalState.update { it.copy(selectedPresetId = null, currentTeaName = "나만의 차") }
                }
            } else if (result is DataResourceResult.Failure) {
                sendEffect(TimerSideEffect.ShowToast(
                    UiText.StringResource(R.string.msg_preset_delete_fail)
                ))
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

            sendEffect(TimerSideEffect.ShowToast(
                UiText.StringResource(R.string.msg_infusion_recorded, nextCount)
            ))

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
        sendEffect(TimerSideEffect.ShowToast(
            UiText.StringResource(R.string.msg_default_preset_warning)
        ))
    }

    private fun sendEffect(effect: TimerSideEffect) {
        viewModelScope.launch {
            _sideEffect.send(effect)
        }
    }
}