package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.HomeContentDto
import com.subin.leafy.data.model.dto.HomeBannerDto
import com.subin.leafy.data.model.dto.QuickBrewingGuideDto
import com.subin.leafy.domain.model.HomeContent
import com.subin.leafy.domain.model.HomeBanner
import com.subin.leafy.domain.model.QuickBrewingGuide

fun HomeContentDto.toDomain(): HomeContent {
    return HomeContent(
        banners = this.banners.map { it.toDomain() },
        quickGuide = this.quickGuide?.toDomain()
    )
}

fun HomeBannerDto.toDomain() = HomeBanner(
    id = this.id,
    title = this.title,
    description = this.description,
    imageUrl = this.imageUrl,
    linkUrl = this.linkUrl,
    label = this.label
)

fun QuickBrewingGuideDto.toDomain() = QuickBrewingGuide(
    id = this.id,
    title = this.title,
    temperature = this.temperature,
    steepingTimeSeconds = this.steepingTimeSeconds,
    amountGrams = this.amountGrams,
    linkUrl = this.linkUrl
)