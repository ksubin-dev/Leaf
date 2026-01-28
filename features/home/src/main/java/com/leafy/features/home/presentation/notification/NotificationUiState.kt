package com.leafy.features.home.presentation.notification

import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.model.Notification

data class NotificationUiState(
    val isLoading: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val errorMessage: UiText? = null
)

sealed interface NotificationSideEffect {
    data class NavigateToPost(val postId: String) : NotificationSideEffect
    data class NavigateToProfile(val userId: String) : NotificationSideEffect
    data class ShowSnackbar(val message: UiText) : NotificationSideEffect
}