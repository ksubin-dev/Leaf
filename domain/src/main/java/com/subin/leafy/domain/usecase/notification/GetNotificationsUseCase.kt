package com.subin.leafy.domain.usecase.notification

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Notification
import com.subin.leafy.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow

class GetNotificationsUseCase(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(): Flow<DataResourceResult<List<Notification>>> {
        return notificationRepository.getNotificationsFlow()
    }
}