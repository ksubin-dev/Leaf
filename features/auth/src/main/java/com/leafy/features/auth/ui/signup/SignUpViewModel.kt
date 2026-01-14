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
    private val userUseCases: UserUseCases,
    private val imageUseCases: ImageUseCases,
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

        // 비밀번호 확인
        if (!state.isPasswordMatching) {
            _uiState.update { it.copy(errorMessage = "비밀번호가 일치하지 않습니다.") }
            return
        }

        // 로딩 시작
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            // 1. 계정 생성
            val authResult = authUseCases.signUp(
                email = state.email,
                password = state.password,
                nickname = state.username
            )

            if (authResult is DataResourceResult.Failure) {
                onError(authResult.exception.message ?: "계정 생성 실패")
                return@launch
            }

            val createdUser = (authResult as DataResourceResult.Success).data

            // 2. 이미지 압축 및 업로드
            var uploadedImageUrl: String? = null

            if (state.profileImageUri != null) {
                try {
                    val compressedUriString = imageCompressor.compressImage(state.profileImageUri.toString())

                    val imageResult = imageUseCases.uploadImage(
                        uri = compressedUriString,
                        folder = "profile_images/${createdUser.id}"
                    )

                    when (imageResult) {
                        is DataResourceResult.Success -> {
                            uploadedImageUrl = imageResult.data
                        }
                        is DataResourceResult.Failure -> {
                            onError("이미지 업로드 실패: ${imageResult.exception.message}")
                            return@launch
                        }
                        else -> {}
                    }
                } catch (e: Exception) {
                    onError("이미지 처리 중 오류 발생: ${e.message}")
                    return@launch
                }
            }

            // 3. 프로필 정보 업데이트
            val updateResult = userUseCases.updateProfile(
                nickname = state.username,
                profileUrl = uploadedImageUrl,
                bio = null
            )

            when (updateResult) {
                is DataResourceResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isSignUpSuccess = true) }
                }
                is DataResourceResult.Failure -> {
                    onError(updateResult.exception.message ?: "프로필 설정 실패")
                }
                else -> {}
            }
        }
    }

    private fun onError(message: String) {
        _uiState.update { it.copy(isLoading = false, errorMessage = message) }
    }
}