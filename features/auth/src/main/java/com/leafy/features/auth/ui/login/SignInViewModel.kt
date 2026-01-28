package com.leafy.features.auth.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.AuthUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.SettingUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val noteUseCases: NoteUseCases,
    private val userUseCases: UserUseCases,
    private val settingUseCases: SettingUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<SignInSideEffect>()
    val sideEffect: Flow<SignInSideEffect> = _sideEffect.receiveAsFlow()

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
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onAutoLoginChecked(checked: Boolean) {
        _uiState.update { it.copy(isAutoLogin = checked) }
    }

    fun signIn() {
        val state = uiState.value
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            when (val result = authUseCases.login(state.email, state.password)) {
                is DataResourceResult.Success -> {
                    Log.d("SignIn", "로그인 성공! 데이터 동기화 시작")
                    handleLoginSuccess(state)
                }
                is DataResourceResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false) }

                    val msg = result.exception.message ?: "로그인에 실패했습니다."
                    sendEffect(SignInSideEffect.ShowSnackbar(msg))
                }
                else -> {}
            }
        }
    }

    private suspend fun handleLoginSuccess(state: SignInUiState) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                noteUseCases.syncNotes()
            }.join()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        settingUseCases.manageLoginSetting.setAutoLogin(state.isAutoLogin)
        settingUseCases.manageLoginSetting.saveEmail(state.email)

        try {
            val myProfileResult = userUseCases.getMyProfile().first()
            if (myProfileResult is DataResourceResult.Success) {
                val user = myProfileResult.data
                val isServerAgreed = user.isNotificationAgreed
                settingUseCases.updateNotificationSetting.setNotificationAgreed(isServerAgreed)
                userUseCases.updateFcmToken(isServerAgreed)
            }
        } catch (e: Exception) {
            Log.e("SignIn", "FCM 동기화 오류: ${e.message}")
        }

        _uiState.update { it.copy(isLoading = false) }
        sendEffect(SignInSideEffect.NavigateToHome)
    }

    private fun sendEffect(effect: SignInSideEffect) {
        viewModelScope.launch {
            _sideEffect.send(effect)
        }
    }
}