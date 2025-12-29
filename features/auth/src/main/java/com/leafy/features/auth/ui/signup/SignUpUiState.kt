package com.leafy.features.auth.ui.signup

import android.net.Uri // URI를 가져오기 위해 필요

data class SignUpUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "", // 비밀번호 확인 필드 추가
    val profileImageUri: Uri? = null, // 선택된 프로필 이미지 URI
    val isProcessing: Boolean = false,
    val userMessage: String? = null,
    val isSignUpSuccess: Boolean = false,
    val isPasswordMatching: Boolean = true
)