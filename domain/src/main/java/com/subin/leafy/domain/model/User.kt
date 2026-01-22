package com.subin.leafy.domain.model

// 유저 도메인 모델
data class User(
    val id: String,
    val nickname: String,
    val profileImageUrl: String?,
    val bio: String?,
    val masterTitle: String? = null,
    val expertTypes: List<TeaType> = emptyList(),

    val socialStats: UserSocialStatistics,
    val relationState: UserRelationState,

    val postCount: Int = 0,
    val followingIds: List<String>,
    val likedPostIds: List<String>,
    val bookmarkedPostIds: List<String>,
    val isNotificationAgreed: Boolean = false,
    val createdAt: Long
)

data class UserSocialStatistics(
    val followerCount: Int = 0,
    val followingCount: Int = 0
)

data class UserRelationState(
    val isFollowing: Boolean = false
)

data class UserAnalysis(
    val totalBrewingCount: Int,
    val currentStreakDays: Int,
    val monthlyBrewingCount: Int,

    val preferredTimeSlot: String,
    val averageBrewingTime: String,
    val preferredTemperature: Int,
    val preferredBrewingSeconds: Int,

    val weeklyCount: Int,
    val dailyCaffeineAvg: Int,
    val weeklyCaffeineTrend: List<Int>,

    val averageRating: Double,
    val favoriteTeaType: String?,
    val teaTypeDistribution: Map<String, Double>,

)