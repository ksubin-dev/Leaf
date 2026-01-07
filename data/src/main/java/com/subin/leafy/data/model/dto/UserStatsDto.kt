package com.subin.leafy.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserStatsDto(
    // 1. 기본 활동 통계
    val totalBrewingCount: Int = 0,
    val currentStreakDays: Int = 0,
    val monthlyBrewingCount: Int = 0,
    val lastMonthBrewingCount: Int = 0, //지난달보다 20% 더 기록하셨어요

    // 2. 브루잉 습관
    val mostFrequentTimeSlot: String = "-",
    val avgBrewTimeSeconds: Int = 0,
    val favoriteTemperature: Int = 0,
    val favoriteBrewTimeSeconds: Int = 0,

    // 3. 웰니스 및 주간 리포트
    val weeklyBrewingCount: Int = 0,
    val dailyCaffeineAvgMg: Int = 0,
    val weeklyCaffeineTrendMg: List<Int> = emptyList(),

    // 4. 선호도 분석
    val avgRating: Double = 0.0,
    val teaTypeDistribution: Map<String, Double> = emptyMap(),
    val favoriteTeaName: String? = null,
    val topFlavorNote: String = "",

    // 5. 자산 상태 및 업데이트 시각
    val teaInventoryCount: Int = 0,
    val wishlistCount: Int = 0,
    val lastUpdatedAt: Long = System.currentTimeMillis()
)