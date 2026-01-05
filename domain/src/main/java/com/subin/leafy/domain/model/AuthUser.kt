package com.subin.leafy.domain.model

data class AuthUser(
    val id: String,
    val email: String,
    val username: String? = null,
    val profileUrl: String? = null,
    val followingIds: List<String> = emptyList(),
    val likedPostIds: List<String> = emptyList(),
    val savedPostIds: List<String> = emptyList()
)
