package com.subin.leafy.domain.model

data class User(
    val id: String,
    val username: String,
    val profileImageUrl: String?
)

// 2. 마이페이지/UI용 통계 정보
data class UserStats(
    val weeklyBrewingCount: Int,
    val averageRating: Double,
    val preferredTea: String,
    val averageBrewingTime: String,
    val monthlyBrewingCount: Int
)