package com.leafy.features.auth.ui.login

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val isAutoLogin: Boolean = false,

    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,
    val errorMessage: String? = null
)