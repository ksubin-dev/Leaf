package com.leafy.features.auth.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.AuthUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.SettingUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import com.subin.leafy.domain.usecase.setting.ManageLoginSettingUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val authUseCases: AuthUseCases,
    private val noteUseCases: NoteUseCases,
    private val userUseCases: UserUseCases,
    private val settingUseCases: SettingUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun loadInitialSettings() {
        viewModelScope.launch {
            val emailResult = settingUseCases.manageLoginSetting.getLastEmail()
            if (emailResult is DataResourceResult.Success) {
                val savedEmail = emailResult.data ?: ""
                _uiState.update { it.copy(email = savedEmail) }
            }

            launch {
                settingUseCases.manageLoginSetting.getAutoLogin().collect { isEnabled ->
                    _uiState.update { it.copy(isAutoLogin = isEnabled) }
                }
            }
        }
    }


    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onAutoLoginChecked(checked: Boolean) {
        _uiState.update { it.copy(isAutoLogin = checked) }
    }

    fun userMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun signIn() {
        val state = uiState.value

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "이메일과 비밀번호를 모두 입력해주세요.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = authUseCases.login(state.email, state.password)) {
                is DataResourceResult.Success -> {
                    Log.d("SYNC_LOG", "로그인 성공! 동기화 요청")

                    // 1. 노트 동기화 (기다림)
                    launch(Dispatchers.IO) {
                        try {
                            noteUseCases.syncNotes()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.join()

                    settingUseCases.manageLoginSetting.setAutoLogin(state.isAutoLogin)
                    settingUseCases.manageLoginSetting.saveEmail(state.email)

                    try {
                        val myProfileResult = userUseCases.getMyProfile().first()

                        if (myProfileResult is DataResourceResult.Success) {
                            val user = myProfileResult.data
                            val isServerAgreed = user.isNotificationAgreed
                            settingUseCases.updateNotificationSetting.setNotificationAgreed(isServerAgreed)
                            userUseCases.updateFcmToken(isServerAgreed)

                            Log.d("FCM", "서버 설정($isServerAgreed)에 따라 로컬 설정 및 토큰 동기화 완료")
                        }
                    } catch (e: Exception) {
                        Log.e("FCM", "동기화 중 오류 발생: ${e.message}")
                    }

                    _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
                }
                is DataResourceResult.Failure -> {
                    val msg = result.exception.message ?: "로그인에 실패했습니다."
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = msg)
                    }
                }
                else -> {}
            }
        }
    }
}