package com.leafy.features.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.subin.leafy.domain.usecase.HomeUseCases
import com.subin.leafy.domain.usecase.NotificationUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases

class HomeViewModelFactory(
    private val homeUseCases: HomeUseCases,
    private val postUseCases: PostUseCases,
    private val userUseCases: UserUseCases,
    private val notificationUseCases: NotificationUseCases
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                homeUseCases = homeUseCases,
                postUseCases = postUseCases,
                userUseCases = userUseCases,
                notificationUseCases = notificationUseCases
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}