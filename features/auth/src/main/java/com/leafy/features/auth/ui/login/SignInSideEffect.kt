package com.leafy.features.auth.ui.login

sealed interface SignInSideEffect {
    data object NavigateToHome : SignInSideEffect
    data class ShowSnackbar(val message: String) : SignInSideEffect
}