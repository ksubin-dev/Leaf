package com.subin.leafy.domain.model

data class Notification(
    val id: String,
    val type: NotificationType,
    val senderId: String,
    val senderName: String,
    val senderProfileUrl: String?,

    // 팔로우 알림이면 null, 게시글 관련이면 PostId가 들어감
    val targetPostId: String? = null,

    // 3. 메시지 내용
    val message: String, // 예: "수빈님의 게시글에 댓글을 남겼습니다."

    // 4. 상태 및 시간
    val isRead: Boolean = false,
    val createdAt: Long
)

enum class NotificationType {
    LIKE,       // 누군가 내 글에 좋아요를 누름
    COMMENT,    // 누군가 내 글에 댓글을 남김
    FOLLOW,     // 누군가 나를 팔로우함
    SYSTEM      // 공지사항이나 앱 시스템 알림
}