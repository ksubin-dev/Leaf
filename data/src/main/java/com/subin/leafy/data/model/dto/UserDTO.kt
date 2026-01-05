package com.subin.leafy.data.model.dto

data class UserDTO(
    val uid: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val bio: String? = "",
    val followerCount: Int = 0,
    val followingCount: Int = 0,
    val likedPostIds: List<String> = emptyList(),
    val savedPostIds: List<String> = emptyList(),
    val followingIds: List<String> = emptyList()
)