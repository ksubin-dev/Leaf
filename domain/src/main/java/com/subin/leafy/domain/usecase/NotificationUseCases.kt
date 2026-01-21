package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.notification.DeleteNotificationUseCase
import com.subin.leafy.domain.usecase.notification.GetNotificationsUseCase
import com.subin.leafy.domain.usecase.notification.MarkNotificationAsReadUseCase

data class NotificationUseCases(
    val getNotifications: GetNotificationsUseCase,
    val markAsRead: MarkNotificationAsReadUseCase,
    val deleteNotification: DeleteNotificationUseCase
)