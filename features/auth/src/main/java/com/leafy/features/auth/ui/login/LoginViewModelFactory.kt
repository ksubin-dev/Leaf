package com.leafy.features.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.subin.leafy.domain.usecase.AuthUseCases

class LoginViewModelFactory(
    private val authUseCases: AuthUseCases
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authUseCases) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}