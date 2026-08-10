package com.subin.leafy.data.mapper

import com.subin.leafy.data.datasource.local.room.entity.UploadQueueEntity
import com.subin.leafy.domain.model.UploadQueue
import com.subin.leafy.domain.model.UploadStatus
import com.subin.leafy.domain.model.UploadTargetType

fun UploadQueueEntity.toDomain(): UploadQueue {
    return UploadQueue(
        id = id,
        targetType = runCatching { UploadTargetType.valueOf(targetType) }.getOrDefault(UploadTargetType.NOTE),
        targetId = targetId,
        status = runCatching { UploadStatus.valueOf(status) }.getOrDefault(UploadStatus.FAILED),
        attempt = attempt,
        maxAttempts = maxAttempts,
        payload = payload,
        message = message,
        lastError = lastError,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastNotifiedAt = lastNotifiedAt
    )
}

fun UploadQueue.toEntity(): UploadQueueEntity {
    return UploadQueueEntity(
        id = id,
        targetType = targetType.name,
        targetId = targetId,
        status = status.name,
        attempt = attempt,
        maxAttempts = maxAttempts,
        payload = payload,
        message = message,
        lastError = lastError,
        createdAt = createdAt,
        updatedAt = updatedAt,
        lastNotifiedAt = lastNotifiedAt
    )
}
