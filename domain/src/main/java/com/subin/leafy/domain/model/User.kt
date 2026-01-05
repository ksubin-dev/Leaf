package com.subin.leafy.domain.model

data class User(
    val id: String,
    val username: String,
    val profileImageUrl: String?,
    val bio: String? = null,
    val followerCount: Int = 0,
    val followingCount: Int = 0
) {
    companion object {
        fun empty(id: String = "") = User(
            id = id,
            username = "사용자",
            profileImageUrl = null,
            bio = "자기소개가 없습니다.",
            followerCount = 0,
            followingCount = 0
        )
    }
}

data class UserStats(
    val totalBrewingCount: Int,
    val currentStreak: Int,
    val monthlyBrewingCount: Int,
    val preferredTimeSlot: String,
    val averageBrewingTime: String,
    val weeklyBrewingCount: Int,
    val averageRating: Double,
    val myTeaChestCount: Int,
    val wishlistCount: Int
) {
    companion object {
        fun empty() = UserStats(
            totalBrewingCount = 0,
            currentStreak = 0,
            monthlyBrewingCount = 0,
            preferredTimeSlot = "-",
            averageBrewingTime = "0:00",
            weeklyBrewingCount = 0,
            averageRating = 0.0,
            myTeaChestCount = 0,
            wishlistCount = 0
        )
    }
}