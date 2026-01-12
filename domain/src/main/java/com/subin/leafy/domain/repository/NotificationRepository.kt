package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    // 1. 내 알림 목록 실시간 조회
    fun getNotificationsFlow(): Flow<DataResourceResult<List<Notification>>>

    // 2. 알림 읽음 처리 (클릭 시)
    suspend fun markAsRead(notificationId: String): DataResourceResult<Unit>
}