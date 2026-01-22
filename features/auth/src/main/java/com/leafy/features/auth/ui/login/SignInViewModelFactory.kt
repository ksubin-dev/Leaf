package com.leafy.features.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.subin.leafy.domain.usecase.AuthUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.SettingUseCases
import com.subin.leafy.domain.usecase.UserUseCases

class SignInViewModelFactory(
    private val authUseCases: AuthUseCases,
    private val noteUseCases: NoteUseCases,
    private val userUseCases: UserUseCases,
    private val settingUseCases: SettingUseCases
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java)) {
            return SignInViewModel(
                authUseCases = authUseCases,
                noteUseCases = noteUseCases,
                userUseCases = userUseCases,
                settingUseCases = settingUseCases
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}