package com.leafy.features.auth.ui.signup

sealed interface SignUpSideEffect {
    data object NavigateToHome : SignUpSideEffect
    data class ShowSnackbar(val message: String) : SignUpSideEffect
    data class ShowErrorDialog(val message: String) : SignUpSideEffect
}