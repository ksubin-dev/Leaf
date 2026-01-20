package com.leafy.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases

class SearchViewModelFactory(
    private val postUseCases: PostUseCases,
    private val userUseCases: UserUseCases
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(postUseCases, userUseCases) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        fun provide(postUseCases: PostUseCases, userUseCases: UserUseCases): SearchViewModelFactory {
            return SearchViewModelFactory(postUseCases, userUseCases)
        }
    }
}