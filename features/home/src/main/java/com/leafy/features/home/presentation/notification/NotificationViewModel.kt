package com.leafy.features.home.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Notification
import com.subin.leafy.domain.model.NotificationType
import com.subin.leafy.domain.usecase.NotificationUseCases
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface NotificationUiEvent {
    data class NavigateToPost(val postId: String) : NotificationUiEvent
    data class NavigateToProfile(val userId: String) : NotificationUiEvent
    data class ShowMessage(val message: String) : NotificationUiEvent
}

class NotificationViewModel(
    private val notificationUseCases: NotificationUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<NotificationUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            notificationUseCases.getNotifications().collectLatest { result ->
                when (result) {
                    is DataResourceResult.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                notifications = result.data,
                                errorMessage = null
                            )
                        }
                    }
                    is DataResourceResult.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.exception.message ?: "알림 로드 실패"
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun onNotificationClick(notification: Notification) {
        viewModelScope.launch {
            if (!notification.isRead) {
                _uiState.update { currentState ->
                    val updatedList = currentState.notifications.map { item ->
                        if (item.id == notification.id) item.copy(isRead = true) else item
                    }
                    currentState.copy(notifications = updatedList)
                }

                notificationUseCases.markAsRead(notification.id)
            }

            when (notification.type) {
                NotificationType.LIKE, NotificationType.COMMENT -> {
                    notification.targetPostId?.let { postId ->
                        _uiEvent.emit(NotificationUiEvent.NavigateToPost(postId))
                    }
                }
                NotificationType.FOLLOW -> {
                    _uiEvent.emit(NotificationUiEvent.NavigateToProfile(notification.senderId))
                }
                NotificationType.SYSTEM -> {

                }
            }
        }
    }

    fun onDeleteNotification(notificationId: String) {
        viewModelScope.launch {

            when (val result = notificationUseCases.deleteNotification(notificationId)) {
                is DataResourceResult.Success -> {
                    _uiEvent.emit(NotificationUiEvent.ShowMessage("알림이 삭제되었습니다."))
                }
                is DataResourceResult.Failure -> {
                    _uiEvent.emit(NotificationUiEvent.ShowMessage("삭제 실패: ${result.exception.message}"))
                }
                else -> {}
            }
        }
    }
}