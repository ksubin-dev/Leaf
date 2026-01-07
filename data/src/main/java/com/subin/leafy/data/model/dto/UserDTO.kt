package com.subin.leafy.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val uid: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val bio: String? = "",
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val fcmToken: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
