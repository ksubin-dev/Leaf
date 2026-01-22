package com.leafy.features.home.presentation.notification

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leafy.shared.di.ApplicationContainer

@Composable
fun makeNotificationViewModelFactory(
    container: ApplicationContainer
): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NotificationViewModel(
                notificationUseCases = container.notificationUseCases
            ) as T
        }
    }
}