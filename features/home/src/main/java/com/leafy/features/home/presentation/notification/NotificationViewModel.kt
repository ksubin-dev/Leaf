package com.leafy.features.home.presentation.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Notification
import com.subin.leafy.domain.model.NotificationType
import com.subin.leafy.domain.usecase.NotificationUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationUseCases: NotificationUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<NotificationSideEffect>()
    val sideEffect: Flow<NotificationSideEffect> = _sideEffect.receiveAsFlow()

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
                                errorMessage = UiText.DynamicString(result.exception.message ?: "알림 로드 실패")
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
                notificationUseCases.markAsRead(notification.id)
            }

            when (notification.type) {
                NotificationType.LIKE, NotificationType.COMMENT -> {
                    notification.targetPostId?.let { postId ->
                        _sideEffect.send(NotificationSideEffect.NavigateToPost(postId))
                    }
                }
                NotificationType.FOLLOW -> {
                    _sideEffect.send(NotificationSideEffect.NavigateToProfile(notification.senderId))
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
                    _sideEffect.send(NotificationSideEffect.ShowSnackbar(UiText.DynamicString("알림이 삭제되었습니다.")))
                }
                is DataResourceResult.Failure -> {
                    val msg = result.exception.message ?: "삭제 실패"
                    _sideEffect.send(NotificationSideEffect.ShowSnackbar(UiText.DynamicString(msg)))
                }
                else -> {}
            }
        }
    }
}