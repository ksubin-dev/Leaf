package com.subin.leafy.domain.model

data class Notification(
    val id: String,
    val type: NotificationType,
    val senderId: String,
    val senderName: String,
    val senderProfileUrl: String?,

    val targetPostId: String? = null,


    val message: String,


    val isRead: Boolean = false,
    val createdAt: Long
)

enum class NotificationType {
    LIKE,
    COMMENT,
    FOLLOW,
    SYSTEM
}