package com.subin.leafy.data.model.dto


data class CommunityPostDTO(
    val _id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorProfileUrl: Int? = null, // 실제 서버 연동 시 String(URL)으로 변경
    val title: String = "",
    val subtitle: String = "",
    val content: String = "",
    val teaTag: String = "",
    val imageRes: Int = 0,
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
    val profileImageRes: Int = 0,
    val isFollowing: Boolean = false
)

// 커뮤니티 태그 DTO
data class CommunityTagDTO(
    val id: String = "",
    val label: String = "",
    val isTrendingUp: Boolean = true
)