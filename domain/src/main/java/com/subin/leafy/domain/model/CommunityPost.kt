package com.subin.leafy.domain.model

data class CommunityPost(
    val id: String,
    val author: PostAuthor,
    val imageUrls: List<String>,
    val title: String,
    val content: String,
    val originNoteId: String? = null,
    val teaType: TeaType? = null,
    val rating: Int? = null,
    val tags: List<String> = emptyList(),
    val brewingSummary: String? = null,    // "95℃ · 3m · 5g"

    val stats: PostStatistics,
    val myState: PostSocialState,
    val topComment: Comment? = null,
    val createdAt: Long
)

data class PostAuthor(
    val id: String,
    val nickname: String,
    val profileImageUrl: String?,
    val isFollowing: Boolean = false
)

