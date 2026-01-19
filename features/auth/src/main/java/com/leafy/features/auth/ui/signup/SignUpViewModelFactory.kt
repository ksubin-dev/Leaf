package com.leafy.features.auth.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.domain.usecase.AuthUseCases

class SignUpViewModelFactory(
    private val authUseCases: AuthUseCases,
    private val imageCompressor: ImageCompressor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(
                authUseCases = authUseCases,
                imageCompressor = imageCompressor
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}