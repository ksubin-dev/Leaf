package com.subin.leafy.domain.model

// 홈 화면 전체를 정의하는 모델
data class HomeContent(
    val banners: List<HomeBanner>,
    val quickGuide: QuickBrewingGuide?
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
    val id: String,
    val title: String = "빠른 브루잉 가이드",
    val temperature: Int,
    val steepingTimeSeconds: Int,
    val amountGrams: Float,
    val linkUrl: String = ""
)

// 3. 랭킹 아이템
data class RankingItem(
    val rank: Int,           // 1, 2, 3 (UI에서 리스트 인덱스로 처리해도 되지만, 모델에 있으면 편함)
    val postId: String,      // 클릭하면 상세 페이지로 가야 하니까 noteId -> postId
    val teaName: String,
    val teaType: TeaType,
    val rating: Int?,        // 별점 (없을 수도 있음)
    val viewCount: Int,      // "조회수" (정렬 기준이자, (156) 처럼 보여줄 숫자)
    val imageUrl: String?
)