package com.subin.leafy.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val uid: String = "",
    val nickname: String = "",
    val profileImageUrl: String? = null,
    val bio: String? = "",
    val masterTitle: String? = null,

    val expertTypes: List<String> = emptyList(),

    val socialStats: SocialStatsDto = SocialStatsDto(),

    val postCount: Int = 0,

    val followingIds: List<String> = emptyList(),
    val likedPostIds: List<String> = emptyList(),
    val bookmarkedPostIds: List<String> = emptyList(),

    val fcmToken: String? = null,
    val isNotificationAgreed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class SocialStatsDto(
    val followerCount: Int = 0,
    val followingCount: Int = 0
)