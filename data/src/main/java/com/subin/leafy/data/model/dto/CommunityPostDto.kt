package com.subin.leafy.data.model.dto


import kotlinx.serialization.Serializable

@Serializable
data class CommunityPostDto(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorProfileUrl: String? = null,

    val title: String = "",
    val content: String = "",
    val imageUrls: List<String> = emptyList(),

    val originNoteId: String? = null,

    // 요약 정보들
    val teaType: String? = null,
    val rating: Int? = null,
    val tags: List<String> = emptyList(),
    val brewingSummary: String? = null,

    // 소셜 통계
    val likeCount: Int = 0,
    val bookmarkCount: Int = 0,
    val commentCount: Int = 0,
    val viewCount: Int = 0,
    val topComment: CommentDto? = null,

    val createdAt: Long = System.currentTimeMillis()
)

