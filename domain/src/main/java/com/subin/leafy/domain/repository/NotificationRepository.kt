package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getNotificationsFlow(): Flow<DataResourceResult<List<Notification>>>

    suspend fun markAsRead(notificationId: String): DataResourceResult<Unit>

    suspend fun deleteNotification(notificationId: String): DataResourceResult<Unit>
}