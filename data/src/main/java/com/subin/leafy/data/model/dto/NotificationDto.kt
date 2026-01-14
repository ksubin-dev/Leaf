package com.subin.leafy.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String = "",
    val type: String = "",             // "LIKE", "COMMENT", "FOLLOW"
    val senderId: String = "",
    val senderName: String = "",       // 닉네임 중복 저장 (성능 최적화)
    val senderProfileUrl: String? = null,
    val targetPostId: String? = null,  // 이동할 게시글 ID
    val message: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)