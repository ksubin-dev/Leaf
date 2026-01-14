package com.subin.leafy.data.datasource.remote

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationDataSource {
    fun getNotifications(userId: String): Flow<DataResourceResult<List<Notification>>>
    suspend fun markAsRead(userId: String, notificationId: String)
}