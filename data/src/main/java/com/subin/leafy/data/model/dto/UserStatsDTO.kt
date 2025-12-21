package com.subin.leafy.data.model.dto

data class UserStatsDTO(
    val weeklyCount: Int = 0,
    val monthlyCount: Int = 0,
    val avgRating: Double = 0.0,
    val favoriteTea: String = "",
    val avgBrewingTime: String = ""
)