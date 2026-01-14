package com.subin.leafy.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class HomeContentDto(
    // 1. 배너 리스트
    val banners: List<HomeBannerDto> = emptyList(),

    // 2. 퀵 가이드 (관리자 추천 팁)
    val quickGuide: QuickBrewingGuideDto? = null,
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
    val id: String = "",
    val title: String = "빠른 브루잉 가이드",
    val temperature: Int = 0,
    val steepingTimeSeconds: Int = 0,
    val amountGrams: Float = 0f,
    val linkUrl: String = ""
)