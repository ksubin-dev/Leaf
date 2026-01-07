package com.subin.leafy.domain.model

// 홈 화면 전체를 정의하는 모델
data class HomeContent(
    val banners: List<HomeBanner>,       // 상단 "Tea of the Month" 배너
    val quickGuide: QuickBrewingGuide?,  // 빠른 브루잉 가이드
    val todayRecommendation: TodayRecommendation?,
    val popularRankings: List<RankingItem> // 지금 인기 있는 시음 기록 Top 3
)

// 1. 상단 배너 모델
data class HomeBanner(
    val id: String,
    val title: String,       // "Tea of the Month"
    val description: String, // "Discover premium Dragon Well"
    val imageUrl: String,
    val linkUrl: String,     // 클릭 시 이동할 경로
    val label: String? = null // "Limited Edition"
)

// 2. 빠른 브루잉 가이드
data class QuickBrewingGuide(
    val title: String = "빠른 브루잉 가이드",
    val temperature: Int,
    val steepingTimeSeconds: Int,
    val amountGrams: Float
)

// '오늘의 픽'에 들어갈 수 있는 데이터들
sealed interface TodayRecommendation {
    data class FeaturedPost(val post: CommunityPost) : TodayRecommendation
    data class RecommendedMaster(val master: TeaMaster) : TodayRecommendation
}

// 3. 랭킹 아이템
data class RankingItem(
    val rank: Int,           // 1, 2, 3
    val noteId: String,      // 상세 페이지 이동용
    val teaName: String,
    val teaType: TeaType,
    val origin: String,
    val rating: Int,
    val reviewCount: Int,
    val imageUrl: String?
)