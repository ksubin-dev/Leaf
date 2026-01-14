package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.UserStatsDTO
import com.subin.leafy.domain.model.UserStats

fun UserStatsDTO.toDomain() = UserStats(
    weeklyBrewingCount = this.weeklyCount,
    monthlyBrewingCount = this.monthlyCount,
    averageRating = this.avgRating,
    preferredTea = this.favoriteTea,
    averageBrewingTime = this.avgBrewingTime
)