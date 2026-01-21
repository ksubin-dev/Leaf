package com.leafy.features.home.notification

import com.subin.leafy.domain.model.Notification

data class NotificationUiState(
    val isLoading: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val errorMessage: String? = null
)