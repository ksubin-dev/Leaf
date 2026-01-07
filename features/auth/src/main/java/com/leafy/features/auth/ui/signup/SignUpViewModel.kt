package com.leafy.features.auth.ui.signup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.AuthUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authUseCases: AuthUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onUsernameChanged(value: String) = _uiState.update { it.copy(username = value) }

    fun onEmailChanged(value: String) = _uiState.update { it.copy(email = value) }

    fun onPasswordChanged(value: String) = _uiState.update {
        it.copy(
            password = value,
            isPasswordMatching = value == it.confirmPassword
        )
    }

    fun onConfirmPasswordChanged(value: String) = _uiState.update {
        it.copy(
            confirmPassword = value,
            isPasswordMatching = it.password == value
        )
    }

    fun onProfileImageSelected(uri: Uri?) = _uiState.update { it.copy(profileImageUri = uri) }

    fun messageShown() = _uiState.update { it.copy(userMessage = null) }

    fun signUp() {
        val currentState = _uiState.value
        if (currentState.email.isBlank() || currentState.password.isBlank() || currentState.username.isBlank()) {
            _uiState.update { it.copy(userMessage = "모든 정보를 입력해주세요.") }
            return
        }

        viewModelScope.launch {
            if (!currentState.isPasswordMatching) {
                _uiState.update { it.copy(userMessage = "비밀번호가 일치하지 않습니다.") }
                return@launch
            }

            _uiState.update { it.copy(isProcessing = true, userMessage = null) }

            val result = authUseCases.signUp(
                email = currentState.email,
                password = currentState.password,
                username = currentState.username,
                profileImageUri = currentState.profileImageUri?.toString(),
            )

            when (result) {
                is DataResourceResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            isSignUpSuccess = true,
                            userMessage = "회원가입 성공!"
                        )
                    }
                }
                is DataResourceResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            userMessage = "에러: ${result.exception.message}"
                        )
                    }
                }
                else -> { _uiState.update { it.copy(isProcessing = false) } }
            }
        }
    }
}