package com.subin.leafy.domain.model

data class TeaMaster(
    val id: String,
    val nickname: String,
    val title: String,
    val profileImageUrl: String?,
    val isFollowing: Boolean = false,
    val followerCount: Int = 0,
    val postCount: Int = 0,
    val expertTypes: List<TeaType> = emptyList()
)