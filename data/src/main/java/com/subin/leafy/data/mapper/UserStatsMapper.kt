package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.UserStatsDTO
import com.subin.leafy.domain.model.UserStats

fun UserStatsDTO.toDomain() = UserStats(
    totalBrewingCount = this.totalCount,
    currentStreak = this.streak,
    monthlyBrewingCount = this.monthlyCount,
    preferredTimeSlot = this.timeSlot,
    averageBrewingTime = this.avgBrewingTime,
    weeklyBrewingCount = this.weeklyCount,
    averageRating = this.avgRating,
    myTeaChestCount = this.teaChestCount,
    wishlistCount = this.wishCount
)