package com.subin.leafy.data.model.dto

data class UserStatsDTO(
    val totalCount: Int = 0,
    val streak: Int = 0,
    val monthlyCount: Int = 0,
    val timeSlot: String = "-",
    val avgBrewingTime: String = "0:00",
    val weeklyCount: Int = 0,
    val avgRating: Double = 0.0,
    val favoriteTea: String? = null,
    val teaChestCount: Int = 0,
    val wishCount: Int = 0
)