package com.subin.leafy.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class HomeContentDto(
    val banners: List<HomeBannerDto> = emptyList(),
    val quickGuide: QuickBrewingGuideDto? = null,
    val todayPickId: String = "",        // 추천할 대상의 ID
    val todayPickType: String = "POST",  // "POST" 또는 "MASTER"
    val popularRankingNoteIds: List<String> = emptyList() // 랭킹 1, 2, 3위의 ID들
)

@Serializable
data class HomeBannerDto(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val linkUrl: String = "",
    val label: String? = null
)

@Serializable
data class QuickBrewingGuideDto(
    val title: String = "빠른 브루잉 가이드",
    val temperature: Int = 0,
    val steepingTimeSeconds: Int = 0,
    val amountGrams: Float = 0f
)