package com.subin.leafy.domain.model

// 수치 정보 통합
data class SocialStatistics(
    val likeCount: Int = 0,
    val bookmarkCount: Int = 0,
    val commentCount: Int = 0,
    val viewCount: Int = 0
)

// 나의 반응 상태 통합
data class UserSocialState(
    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false
)