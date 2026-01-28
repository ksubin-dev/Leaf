package com.leafy.features.auth.ui.signup

import android.net.Uri

data class SignUpUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val profileImageUri: Uri? = null,

    val isLoading: Boolean = false
) {
    val isPasswordMatching: Boolean
        get() = password.isNotEmpty() && password == confirmPassword
}