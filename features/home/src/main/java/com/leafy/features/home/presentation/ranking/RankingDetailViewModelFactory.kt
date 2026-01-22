package com.leafy.features.home.presentation.ranking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.subin.leafy.domain.usecase.PostUseCases

class RankingDetailViewModelFactory(
    private val postUseCases: PostUseCases
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if (modelClass.isAssignableFrom(RankingDetailViewModel::class.java)) {
            val savedStateHandle = extras.createSavedStateHandle()

            return RankingDetailViewModel(
                savedStateHandle = savedStateHandle,
                postUseCases = postUseCases
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}