package com.subin.leafy.data.model.dto


data class CommunityPostDTO(
    val _id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorProfileUrl: String? = null,
    val title: String = "",
    val subtitle: String = "",
    val content: String = "",
    val teaTag: String = "",
    val imageUrl: String? = null,
    val rating: Float = 0f,
    val metaInfo: String = "",
    val brewingSteps: List<String> = emptyList(),
    val likeCount: Int = 0,
    val savedCount: Int = 0,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val createdAt: String = ""
)

// 티 마스터 DTO
data class TeaMasterDTO(
    val id: String = "",
    val name: String = "",
    val title: String = "",
    val profileImageUrl: String? = null,
    val isFollowing: Boolean = false
)

// 커뮤니티 태그 DTO
data class CommunityTagDTO(
    val id: String = "",
    val label: String = "",
    val isTrendingUp: Boolean = true
)