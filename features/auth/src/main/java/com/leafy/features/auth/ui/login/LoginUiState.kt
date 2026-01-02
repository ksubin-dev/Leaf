package com.leafy.features.auth.ui.login

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isKeepLoggedIn: Boolean = false, // '로그인 상태 유지' 체크박스용
    val isProcessing: Boolean = false,
    val userMessage: String? = null,
    val isLoginSuccess: Boolean = false
)