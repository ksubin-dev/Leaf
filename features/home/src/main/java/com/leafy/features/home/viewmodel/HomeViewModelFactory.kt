package com.leafy.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.subin.leafy.domain.usecase.HomeUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases

class HomeViewModelFactory(
    private val homeUseCases: HomeUseCases,
    private val postUseCases: PostUseCases,
    private val userUseCases: UserUseCases
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                homeUseCases = homeUseCases,
                postUseCases = postUseCases,
                userUseCases = userUseCases
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}