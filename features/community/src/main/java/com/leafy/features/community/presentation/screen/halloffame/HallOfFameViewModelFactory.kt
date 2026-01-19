package com.leafy.features.community.presentation.screen.halloffame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.subin.leafy.domain.usecase.PostUseCases

class HallOfFameViewModelFactory {
    companion object {
        fun provide(postUseCases: PostUseCases): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HallOfFameViewModel(postUseCases) as T
            }
        }
    }
}