package com.leafy.features.community.presentation.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases

class CommunityPostDetailViewModelFactory(
    private val postUseCases: PostUseCases,
    private val userUseCases: UserUseCases
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(CommunityPostDetailViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()

            return CommunityPostDetailViewModel(
                savedStateHandle = savedStateHandle,
                postUseCases = postUseCases,
                userUseCases = userUseCases
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}