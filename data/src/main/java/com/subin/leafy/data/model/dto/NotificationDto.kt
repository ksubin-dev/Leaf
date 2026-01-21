package com.subin.leafy.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String = "",
    val type: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderProfileUrl: String? = null,
    val targetPostId: String? = null,
    val message: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)