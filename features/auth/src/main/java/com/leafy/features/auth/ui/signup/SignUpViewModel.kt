package com.leafy.features.auth.ui.signup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val imageCompressor: ImageCompressor
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<SignUpSideEffect>()
    val sideEffect: Flow<SignUpSideEffect> = _sideEffect.receiveAsFlow()

    fun onUsernameChanged(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword) }
    }

    fun onProfileImageSelected(uri: Uri?) {
        _uiState.update { it.copy(profileImageUri = uri) }
    }

    fun signUp() {
        val state = uiState.value
        if (state.isLoading) return

        if (!state.isPasswordMatching) {
            return
        }

        _uiState.update { it.copy(isLoading = true) }

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
                        _uiState.update { it.copy(isLoading = false) }
                        sendEffect(SignUpSideEffect.NavigateToHome)
                    }
                    is DataResourceResult.Failure -> {
                        _uiState.update { it.copy(isLoading = false) }
                        val message = result.exception.message ?: "회원가입 실패"
                        sendEffect(SignUpSideEffect.ShowErrorDialog(message))
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                sendEffect(SignUpSideEffect.ShowSnackbar("시스템 오류: ${e.message}"))
            }
        }
    }

    private fun sendEffect(effect: SignUpSideEffect) {
        viewModelScope.launch {
            _sideEffect.send(effect)
        }
    }
}