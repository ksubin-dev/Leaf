package com.subin.leafy.domain.model

data class AuthUser(
    val id: String,         // Firebase UID
    val email: String,
    val username: String? = null
)