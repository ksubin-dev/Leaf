package com.subin.leafy.domain.model

// 1. 인사이트 전체 결과 모델
data class BrewingInsight(
    val id: String,
    val type: InsightCategory,
    val emoji: String,
    val title: String,
    val description: String,
    val content: InsightContent,
    val action: InsightAction? = null
)

// 2. 인사이트 클릭 시 수행할 액션
sealed class InsightAction {
    data object StartTimer : InsightAction()             // 타이머 화면으로 이동
    data object GoToWishlist : InsightAction()           // 위시리스트/차고 화면으로 이동
    data class OpenTeaDetail(val teaId: String) : InsightAction() // 특정 차 상세정보 이동
    data object OpenFullAnalytics : InsightAction()      // 전체 분석 리포트 페이지로 이동
    data object RecordToday : InsightAction()            // 오늘 기록하기(노트 작성) 이동
    data class SuggestBrewing(                           // 추천 레시피로 타이머 설정
        val teaType: TeaType,
        val temp: Int,
        val timeSeconds: Int
    ) : InsightAction()
}

// 3. 인사이트 카테고리
enum class InsightCategory {
    BREWING_HABIT,    // 시간대별 패턴 (언제 마시는가)
    TEA_PREFERENCE,   // 차 종류 선호도 (무엇을 마시는가)
    SKILL_ANALYSIS,   // 우림 숙련도 (어떻게 우리는가)
    HEALTH_WELLNESS,  // 카페인/수분 리포트 (몸 상태는 어떤가)
    BEST_EVALUATION   // 최고의 맛 발견 (어떤 게 제일 맛있었나)
}

// 4. 다양한 UI 형태를 지원하기 위한 실드 인터페이스
sealed interface InsightContent {
    // 도넛/바 차트용
    data class ChartData(
        val labels: List<String>,
        val values: List<Float>,
        val unit: String,
        val colors: List<String> = emptyList()
    ) : InsightContent

    // 핵심 수치 요약 ("평균 85℃" 등)
    data class Summary(
        val mainValue: String,
        val subValue: String? = null,
        val trend: TrendType = TrendType.STABLE
    ) : InsightContent

    // 텍스트 기반 추천 카드
    data class Recommendation(
        val title: String,
        val tags: List<String>,
        val message: String
    ) : InsightContent
}

// 5. 추세 표시용
enum class TrendType { UP, DOWN, STABLE }