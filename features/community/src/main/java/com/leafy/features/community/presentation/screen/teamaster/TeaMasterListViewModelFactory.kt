package com.leafy.features.community.presentation.screen.teamaster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases

class TeaMasterListViewModelFactory {
    companion object {
        fun provide(
            postUseCases: PostUseCases,
            userUseCases: UserUseCases
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TeaMasterListViewModel(postUseCases, userUseCases) as T
            }
        }
    }
}