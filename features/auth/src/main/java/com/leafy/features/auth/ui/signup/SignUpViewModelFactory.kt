package com.leafy.features.auth.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leafy.shared.util.ImageCompressor // 쉐어드 유틸 임포트
import com.subin.leafy.domain.usecase.AuthUseCases
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.UserUseCases

class SignUpViewModelFactory(
    private val authUseCases: AuthUseCases,
    private val userUseCases: UserUseCases,
    private val imageUseCases: ImageUseCases,
    private val imageCompressor: ImageCompressor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(
                authUseCases = authUseCases,
                userUseCases = userUseCases,
                imageUseCases = imageUseCases,
                imageCompressor = imageCompressor
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}