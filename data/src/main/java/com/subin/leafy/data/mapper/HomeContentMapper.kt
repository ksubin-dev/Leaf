package com.subin.leafy.data.mapper

import com.subin.leafy.data.model.dto.*
import com.subin.leafy.domain.model.*

// 1. 배너 변환
fun HomeBannerDto.toDomain() = HomeBanner(
    id = id, title = title, description = description,
    imageUrl = imageUrl, linkUrl = linkUrl, label = label
)

// 2. 퀵 가이드 변환
fun QuickBrewingGuideDto.toDomain() = QuickBrewingGuide(
    title = title,
    temperature = temperature,
    steepingTimeSeconds = steepingTimeSeconds,
    amountGrams = amountGrams
)

// 3. 전체 홈 콘텐츠 조립
// 핵심: todayRecommendation과 popularRankings는 Repository가 다른 컬렉션에서 가져와서 넘겨줍니다.
fun HomeContentDto.toHomeDomain(
    todayRecommendation: TodayRecommendation?,
    popularRankings: List<RankingItem>
) = HomeContent(
    banners = this.banners.map { it.toDomain() },
    quickGuide = this.quickGuide?.toDomain(),
    todayRecommendation = todayRecommendation,
    popularRankings = popularRankings
)

/**
 * 시음 노트(BrewingNote) 정보를 랭킹 UI에 맞는 RankingItem으로 변환합니다.
 */
fun BrewingNote.toRankingItem(rank: Int) = RankingItem(
    rank = rank,
    noteId = this.id,
    teaName = this.teaInfo.name,
    teaType = this.teaInfo.type,
    origin = this.teaInfo.origin,
    rating = this.rating.stars, // 이제 둘 다 Int이므로 깔끔하게 매칭!
    reviewCount = this.stats.commentCount,
    imageUrl = this.metadata.imageUrls.firstOrNull()
)