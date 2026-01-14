package com.leafy.features.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.AuthUseCases
import com.subin.leafy.domain.usecase.setting.ManageLoginSettingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    private val authUseCases: AuthUseCases,
    private val manageLoginSettingUseCase: ManageLoginSettingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun loadInitialSettings() {
        viewModelScope.launch {
            val emailResult = manageLoginSettingUseCase.getLastEmail()
            if (emailResult is DataResourceResult.Success) {
                val savedEmail = emailResult.data ?: ""
                _uiState.update { it.copy(email = savedEmail) }
            }

            launch {
                manageLoginSettingUseCase.getAutoLogin().collect { isEnabled ->
                    _uiState.update { it.copy(isAutoLogin = isEnabled) }
                }
            }
        }
    }

    // --- 입력 값 변경 핸들러 ---

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

    // --- 로그인 로직 ---
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
                    launch { manageLoginSettingUseCase.setAutoLogin(state.isAutoLogin) }
                    launch { manageLoginSettingUseCase.saveEmail(state.email) }

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