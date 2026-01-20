package com.leafy.features.mypage.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.AuthUseCases
import com.subin.leafy.domain.usecase.SettingUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingUiState(
    val isNotificationEnabled: Boolean = false,
    val isAutoLoginEnabled: Boolean = false,
    val isDarkTheme: Boolean = false, // (선택) 다크모드
    val appVersion: String = "1.0.0",
    val isLoading: Boolean = false,
    val logoutSuccess: Boolean = false,
    val deleteSuccess: Boolean = false
)

class SettingViewModel(
    private val settingUseCases: SettingUseCases,
    private val authUseCases: AuthUseCases,
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingUseCases.getAppSettings.invoke().collectLatest { appSettings ->
                _uiState.update {
                    it.copy(
                        isNotificationEnabled = appSettings.isNotificationAgreed,
                        isAutoLoginEnabled = appSettings.autoLogin,
                        isDarkTheme = appSettings.isDarkTheme
                    )
                }
            }
        }
    }

    // 알림 설정 토글
    // [수정 완료] 알림 설정 토글
    fun toggleNotification(isEnabled: Boolean) {
        viewModelScope.launch {
            // 1. UI 선반영 (즉각적인 반응)
            _uiState.update { it.copy(isNotificationEnabled = isEnabled) }

            // 2. UseCase 함수 호출 (클래스 인스턴스.함수명)
            settingUseCases.updateNotificationSetting.setNotificationAgreed(isEnabled)
        }
    }

    // 자동 로그인 토글
    fun toggleAutoLogin(isEnabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAutoLoginEnabled = isEnabled) }
            // UseCase 호출
            settingUseCases.manageLoginSetting.setAutoLogin(isEnabled)
        }
    }

    // (선택) 다크 모드 토글
    fun toggleDarkMode(isEnabled: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDarkTheme = isEnabled) }
            settingUseCases.updateDisplaySetting.setDarkMode(isEnabled)
        }
    }

    // 로그아웃
    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = authUseCases.logout() // AuthRepository에서 clearSettings()까지 호출함

            if (result is DataResourceResult.Success) {
                _uiState.update { it.copy(isLoading = false, logoutSuccess = true) }
            } else {
                // 로그아웃 실패 시 처리 (보통은 실패해도 화면 이동 시키긴 함)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // 회원 탈퇴
    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // AuthRepository에서 DB 삭제 + Auth 삭제 + 로컬 설정 초기화까지 수행함
            val result = authUseCases.deleteAccount()

            if (result is DataResourceResult.Success) {
                _uiState.update { it.copy(isLoading = false, deleteSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false) }
                // 에러 처리 (토스트 메시지 등) 필요 시 추가
            }
        }
    }
}