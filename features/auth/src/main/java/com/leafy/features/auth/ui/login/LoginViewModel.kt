package com.leafy.features.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.AuthUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) = _uiState.update { it.copy(email = email) }
    fun onPasswordChanged(password: String) = _uiState.update { it.copy(password = password) }
    fun onKeepLoggedInChanged(checked: Boolean) = _uiState.update { it.copy(isKeepLoggedIn = checked) }

    // 메시지 소비 후 초기화
    fun messageShown() = _uiState.update { it.copy(userMessage = null) }

    fun login() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, userMessage = null) }

            val result = authUseCases.login(
                email = _uiState.value.email,
                password = _uiState.value.password
            )

            when (result) {
                is DataResourceResult.Success -> {
                    _uiState.update { it.copy(isProcessing = false, isLoginSuccess = true) }
                }
                is DataResourceResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            userMessage = result.exception.message ?: "로그인에 실패했습니다."
                        )
                    }
                }
                else -> { _uiState.update { it.copy(isProcessing = false) } }
            }
        }
    }
}