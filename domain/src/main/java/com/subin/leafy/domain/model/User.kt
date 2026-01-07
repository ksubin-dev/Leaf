package com.subin.leafy.domain.model

data class User(
    val id: String,
    val nickname: String,
    val profileImageUrl: String?,
    val bio: String?,
    val followerCount: Int,
    val followingCount: Int,
    val followingIds: List<String>,
    val likedPostIds: List<String>,
    val savedPostIds: List<String>,
    val createdAt: Long
)

data class UserStats(
    // 1. 기본 활동 데이터
    val totalBrewingCount: Int,       // 총 시음 횟수
    val currentStreakDays: Int,       // 연속 기록 일수
    val monthlyBrewingCount: Int,     // 이번 달 기록 수

    // 2. 우림 습관 분석
    val preferredTimeSlot: String,    // 주로 마시는 시간대 (예: "오후 2시 - 4시")
    val averageBrewingTime: String,   // 평균 우림 시간
    val preferredTemperature: Int,    // 가장 평점 높았던 온도 (예: 85)
    val preferredBrewingSeconds: Int, // 가장 평점 높았던 시간 (초)

    // 3. 카페인 & 주간 통계
    val weeklyCount: Int,             // 이번 주 기록 수
    val dailyCaffeineAvg: Int,        // 일일 평균 카페인 섭취량 (mg)
    val weeklyCaffeineTrend: List<Int>, // 월~일 카페인 양 추이 (7개 숫자)

    // 4. 선호도 분석
    val averageRating: Double,        // 전체 평균 평점
    val favoriteTeaType: String?,     // 가장 많이 마신 차 종류 (예: "우롱차")
    val teaTypeDistribution: Map<String, Double>, // 차 종류별 비율 (차트용)

    // 5. 보관함 상태
    val myTeaChestCount: Int,         // 내 차 보관함 개수
    val wishlistCount: Int            // 위시리스트 개수
)