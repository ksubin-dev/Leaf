package com.leafy.features.community.presentation.screen.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.subin.leafy.domain.usecase.PostUseCases

class PopularPostListViewModelFactory {
    companion object {
        fun provide(postUseCases: PostUseCases): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PopularPostListViewModel(postUseCases) as T
            }
        }
    }
}