package com.subin.leafy.data.model.dto


import kotlinx.serialization.Serializable

@Serializable
data class CommunityPostDto(
    val id: String = "",

    // 1. 작성자 정보 (객체로 묶음)
    val author: PostAuthorDto = PostAuthorDto(),

    val title: String = "",
    val content: String = "",
    val imageUrls: List<String> = emptyList(),
    val originNoteId: String? = null,

    // 2. 요약 정보
    val teaType: String? = null,
    val rating: Int? = null,
    val tags: List<String> = emptyList(),
    val brewingSummary: String? = null,

    val stats: PostStatisticsDto = PostStatisticsDto(),
    val topComment: CommentDto? = null,

    val createdAt: Long = System.currentTimeMillis()
)

// [2] 작성자 DTO
@Serializable
data class PostAuthorDto(
    val id: String = "",
    val nickname: String = "",
    val profileImageUrl: String? = null
)

// [3] 통계 DTO
@Serializable
data class PostStatisticsDto(
    val likeCount: Int = 0,
    val bookmarkCount: Int = 0,
    val commentCount: Int = 0,
    val viewCount: Int = 0
)

// [4] 댓글 DTO
@Serializable
data class CommentDto(
    val id: String = "",
    val postId: String = "",
    val author: PostAuthorDto = PostAuthorDto(),

    val content: String = "",
    val createdAt: Long = System.currentTimeMillis()
)