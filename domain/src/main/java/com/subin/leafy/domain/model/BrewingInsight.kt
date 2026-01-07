package com.subin.leafy.domain.model

// 1. 인사이트 전체 결과 모델
data class BrewingInsight(
    val id: String,
    val type: InsightCategory,
    val emoji: String,
    val title: String,
    val description: String,
    val content: InsightContent, // 분석된 상세 데이터 (차트용 또는 요약용)
    val action: InsightAction? = null
)

// 인사이트 클릭 시 수행할 액션 (안드로이드 내비게이션 연결용 데이터)
sealed class InsightAction {
    data object StartTimer : InsightAction()             // 타이머 화면으로 이동
    data object GoToWishlist : InsightAction()           // 위시리스트/차고 화면으로 이동
    data class OpenTeaDetail(val teaId: String) : InsightAction() // 특정 차 상세정보 이동
    data object OpenFullAnalytics : InsightAction()      // 전체 분석 리포트 페이지 이동
    data object RecordToday : InsightAction()            // 오늘 기록하기 화면 이동
    data class SuggestBrewing(                           // 추천 설정으로 타이머 세팅
        val temp: Int,
        val timeSeconds: Int
    ) : InsightAction()
}

// 2. 인사이트 카테고리 (건강/웰니스 추가)
enum class InsightCategory {
    TIME_PATTERN,     // 시간대별 패턴
    TEA_PREFERENCE,   // 차 종류 선호도 (도넛 차트용)
    BREWING_MASTER,   // 우림 숙련도 (평균 온도, 시간)
    WELLNESS_REPORT,  // 건강 리포트 (수분, 카페인 추정 등)
    TASTE_DISCOVERY   // 맛의 발견 (최고 평점 패턴)
}

// 3. 다양한 인사이트 내용을 담기 위한 실드 인터페이스
sealed interface InsightContent {
    // 도넛 차트나 바 차트용 데이터
    data class ChartData(
        val labels: List<String>,
        val values: List<Float>,
        val unit: String
    ) : InsightContent

    // 핵심 수치 요약 (예: "평균 85도", "총 1.2L")
    data class Summary(
        val value: String,
        val subValue: String? = null
    ) : InsightContent

    // 텍스트 기반 추천
    data class Recommendation(
        val tags: List<String>,
        val message: String
    ) : InsightContent
}