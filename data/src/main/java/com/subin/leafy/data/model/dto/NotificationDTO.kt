package com.subin.leafy.data.model.dto

data class NotificationDTO(
    val id: String = "",
    val type: String = "", // "LIKE", "COMMENT", "FOLLOW"
    val senderId: String = "", // 알림을 보낸 사람 ID
    val senderName: String = "", // 보낸 사람 이름
    val senderProfileUrl: String? = null,
    val targetPostId: String? = null, // 좋아요/댓글일 경우 해당 게시글 ID
    val message: String = "", // "OO님이 회원님을 팔로우했습니다" 등
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)