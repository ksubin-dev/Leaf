package com.leafy.features.mypage.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TimerSettings
import com.subin.leafy.domain.usecase.AuthUseCases
import com.subin.leafy.domain.usecase.SettingUseCases
import com.subin.leafy.domain.usecase.TimerUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingUiState(
    val isNotificationEnabled: Boolean = false,
    val isAutoLoginEnabled: Boolean = false,
    val isDarkTheme: Boolean = false,
    val isTimerVibrationOn: Boolean = true,
    val isTimerSoundOn: Boolean = true,
    val isTimerScreenOn: Boolean = true,
    val appVersion: String = "1.0.0",
    val isLoading: Boolean = false,
    val logoutSuccess: Boolean = false,
    val deleteSuccess: Boolean = false,
    val errorMessage: String? = null
)

class SettingViewModel(
    private val settingUseCases: SettingUseCases,
    private val authUseCases: AuthUseCases,
    private val userUseCases: UserUseCases,
    private val timerUseCases: TimerUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState = _uiState.asStateFlow()

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
                _uiState.update { it.copy(errorMessage = "설정 저장 실패: ${result.exception.message}") }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = authUseCases.logout()

            if (result is DataResourceResult.Success) {
                _uiState.update { it.copy(isLoading = false, logoutSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = authUseCases.deleteAccount()

            if (result is DataResourceResult.Success) {
                _uiState.update { it.copy(isLoading = false, deleteSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun messageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}