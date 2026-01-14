package com.subin.leafy.domain.usecase.notification

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.NotificationRepository

class MarkNotificationAsReadUseCase(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(notificationId: String): DataResourceResult<Unit> {
        if (notificationId.isBlank()) {
            return DataResourceResult.Failure(Exception("유효하지 않은 알림 ID입니다."))
        }

        return notificationRepository.markAsRead(notificationId)
    }
}