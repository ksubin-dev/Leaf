package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.NotificationDto
import com.subin.leafy.domain.model.Notification
import com.subin.leafy.domain.model.NotificationType


// 1. DTO -> Domain (알림 목록 불러오기)
fun NotificationDto.toNotificationDomain() = Notification(
    id = this.id,
    type = runCatching { NotificationType.valueOf(this.type) }.getOrDefault(NotificationType.SYSTEM),
    senderId = this.senderId,
    senderName = this.senderName,
    senderProfileUrl = this.senderProfileUrl,
    targetPostId = this.targetPostId,
    message = this.message,
    isRead = this.isRead,
    createdAt = this.createdAt
)

// 2. Domain -> DTO (알림 상태 업데이트/저장 시)
fun Notification.toDto() = NotificationDto(
    id = this.id,
    type = this.type.name,
    senderId = this.senderId,
    senderName = this.senderName,
    senderProfileUrl = this.senderProfileUrl,
    targetPostId = this.targetPostId,
    message = this.message,
    isRead = this.isRead,
    createdAt = this.createdAt
)

/**
 * 리스트 변환 헬퍼
 */
fun List<NotificationDto>.toNotificationDomainList() = this.map { it.toNotificationDomain() }