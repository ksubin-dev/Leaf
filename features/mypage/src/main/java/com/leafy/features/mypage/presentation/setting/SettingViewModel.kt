package com.leafy.features.mypage.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.R
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TimerSettings
import com.subin.leafy.domain.usecase.AuthUseCases
import com.subin.leafy.domain.usecase.SettingUseCases
import com.subin.leafy.domain.usecase.TimerUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SettingSideEffect {
    data class ShowToast(val message: UiText) : SettingSideEffect
    data object LogoutSuccess : SettingSideEffect
    data object DeleteAccountSuccess : SettingSideEffect
}

data class SettingUiState(
    val isNotificationEnabled: Boolean = false,
    val isAutoLoginEnabled: Boolean = false,
    val isDarkTheme: Boolean = false,
    val isTimerVibrationOn: Boolean = true,
    val isTimerSoundOn: Boolean = true,
    val isTimerScreenOn: Boolean = true,
    val appVersion: String = "1.0.0",
    val isLoading: Boolean = false
)

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingUseCases: SettingUseCases,
    private val authUseCases: AuthUseCases,
    private val userUseCases: UserUseCases,
    private val timerUseCases: TimerUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<SettingSideEffect>()
    val sideEffect: Flow<SettingSideEffect> = _sideEffect.receiveAsFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            launch {
                settingUseCases.getAppSettings().collectLatest { appSettings ->
                    _uiState.update {
                        it.copy(
                            isNotificationEnabled = appSettings.isNotificationAgreed,
                            isAutoLoginEnabled = appSettings.autoLogin,
                            isDarkTheme = appSettings.isDarkTheme
                        )
                    }
                }
            }

            launch {
                timerUseCases.getTimerSettings().collectLatest { timerSettings ->
                    _uiState.update {
                        it.copy(
                            isTimerVibrationOn = timerSettings.isVibrationOn,
                            isTimerSoundOn = timerSettings.isSoundOn,
                            isTimerScreenOn = timerSettings.keepScreenOn
                        )
                    }
                }
            }
        }
    }

    fun toggleNotification(isEnabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isNotificationEnabled = isEnabled) }
            settingUseCases.updateNotificationSetting.setNotificationAgreed(isEnabled)
            userUseCases.updateFcmToken(isEnabled)
        }
    }

    fun toggleAutoLogin(isEnabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAutoLoginEnabled = isEnabled) }
            settingUseCases.manageLoginSetting.setAutoLogin(isEnabled)
        }
    }

    fun toggleDarkMode(isEnabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDarkTheme = isEnabled) }
            settingUseCases.updateDisplaySetting.setDarkMode(isEnabled)
        }
    }

    fun toggleTimerVibration(isEnabled: Boolean) {
        _uiState.update { it.copy(isTimerVibrationOn = isEnabled) }
        updateTimerSettings { it.copy(isVibrationOn = isEnabled) }
    }

    fun toggleTimerSound(isEnabled: Boolean) {
        _uiState.update { it.copy(isTimerSoundOn = isEnabled) }
        updateTimerSettings { it.copy(isSoundOn = isEnabled) }
    }

    fun toggleTimerScreenOn(isEnabled: Boolean) {
        _uiState.update { it.copy(isTimerScreenOn = isEnabled) }
        updateTimerSettings { it.copy(keepScreenOn = isEnabled) }
    }

    private fun updateTimerSettings(transform: (TimerSettings) -> TimerSettings) {
        viewModelScope.launch {
            val currentSettings = TimerSettings(
                isVibrationOn = _uiState.value.isTimerVibrationOn,
                isSoundOn = _uiState.value.isTimerSoundOn,
                keepScreenOn = _uiState.value.isTimerScreenOn
            )
            val newSettings = transform(currentSettings)

            val result = timerUseCases.updateTimerSettings(newSettings)

            if (result is DataResourceResult.Failure) {
                val msg = result.exception.message
                val uiText = if (msg != null) UiText.DynamicString(msg)
                else UiText.StringResource(R.string.msg_setting_save_fail)
                sendEffect(SettingSideEffect.ShowToast(uiText))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = authUseCases.logout()

            _uiState.update { it.copy(isLoading = false) }

            if (result is DataResourceResult.Success) {
                sendEffect(SettingSideEffect.LogoutSuccess)
            } else {
                sendEffect(SettingSideEffect.ShowToast(UiText.StringResource(R.string.msg_logout_fail)))
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = authUseCases.deleteAccount()

            _uiState.update { it.copy(isLoading = false) }

            if (result is DataResourceResult.Success) {
                sendEffect(SettingSideEffect.DeleteAccountSuccess)
            } else {
                sendEffect(SettingSideEffect.ShowToast(UiText.StringResource(R.string.msg_delete_account_fail)))
            }
        }
    }

    private fun sendEffect(effect: SettingSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }
}