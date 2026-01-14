package com.subin.leafy.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserBadgeDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val obtainedAt: Long = 0L // 획득 날짜
)