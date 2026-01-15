package com.leafy.features.auth.ui.signup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.util.ImageCompressor
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.AuthUseCases
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authUseCases: AuthUseCases,
    private val imageCompressor: ImageCompressor
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onUsernameChanged(username: String) {
        _uiState.update { it.copy(username = username, errorMessage = null) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, errorMessage = null) }
    }

    fun onProfileImageSelected(uri: Uri?) {
        _uiState.update { it.copy(profileImageUri = uri) }
    }

    fun userMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun signUp() {
        val state = uiState.value

        if (state.isLoading || state.isSignUpSuccess) return

        if (!state.isPasswordMatching) {
            _uiState.update { it.copy(errorMessage = "비밀번호가 일치하지 않습니다.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val compressedUri = state.profileImageUri?.let {
                    imageCompressor.compressImage(it.toString())
                }

                val result = authUseCases.signUp(
                    email = state.email,
                    password = state.password,
                    nickname = state.username,
                    profileImageUri = compressedUri
                )

                when (result) {
                    is DataResourceResult.Success -> {
                        _uiState.update { it.copy(isLoading = false, isSignUpSuccess = true) }
                    }
                    is DataResourceResult.Failure -> {
                        onError(result.exception.message ?: "회원가입 실패")
                    }

                    else -> {}
                }
            } catch (e: Exception) {
                onError("알 수 없는 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    private fun onError(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message) }
    }
}