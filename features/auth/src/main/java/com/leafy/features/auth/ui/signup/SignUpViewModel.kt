package com.leafy.features.auth.ui.signup

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.R // 👈 Shared 리소스
import com.leafy.shared.utils.ImageCompressor
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.AuthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SignUpSideEffect {
    data object NavigateToHome : SignUpSideEffect
    data class ShowToast(val message: UiText) : SignUpSideEffect
    data class ShowErrorDialog(val message: UiText) : SignUpSideEffect
}

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
                        sendEffect(SignUpSideEffect.ShowToast(
                            UiText.StringResource(R.string.msg_sign_up_success)
                        ))
                        sendEffect(SignUpSideEffect.NavigateToHome)
                    }
                    is DataResourceResult.Failure -> {
                        _uiState.update { it.copy(isLoading = false) }
                        val message = result.exception.message
                        val uiText = if (message.isNullOrBlank()) {
                            UiText.StringResource(R.string.msg_sign_up_fail)
                        } else {
                            UiText.DynamicString(message)
                        }
                        sendEffect(SignUpSideEffect.ShowErrorDialog(uiText))
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                sendEffect(SignUpSideEffect.ShowToast(
                    UiText.StringResource(R.string.msg_unknown_error)
                ))
            }
        }
    }

    private fun sendEffect(effect: SignUpSideEffect) {
        viewModelScope.launch {
            _sideEffect.send(effect)
        }
    }
}