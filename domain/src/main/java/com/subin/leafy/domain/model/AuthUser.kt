package com.subin.leafy.domain.model

data class AuthUser(
    val id: String,
    val email: String,
    val nickname: String? = null,
    val profileUrl: String? = null,

    val postCount: Int = 0,

    val followingIds: List<String> = emptyList(),
    val likedPostIds: List<String> = emptyList(),
    val bookmarkedPostIds: List<String> = emptyList(),

    val fcmToken: String? = null,
    val isNewUser: Boolean = false,
    val providerId: String? = null
)