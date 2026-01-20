package com.leafy.features.community.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases

class UserProfileViewModelFactory(
    private val userUseCases: UserUseCases,
    private val postUseCases: PostUseCases
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val savedStateHandle = extras.createSavedStateHandle()

        return UserProfileViewModel(
            savedStateHandle = savedStateHandle,
            userUseCases = userUseCases,
            postUseCases = postUseCases
        ) as T
    }
}