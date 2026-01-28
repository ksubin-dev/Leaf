package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.notification.DeleteNotificationUseCase
import com.subin.leafy.domain.usecase.notification.GetNotificationsUseCase
import com.subin.leafy.domain.usecase.notification.MarkNotificationAsReadUseCase
import javax.inject.Inject

data class NotificationUseCases @Inject constructor(
    val getNotifications: GetNotificationsUseCase,
    val markAsRead: MarkNotificationAsReadUseCase,
    val deleteNotification: DeleteNotificationUseCase
)