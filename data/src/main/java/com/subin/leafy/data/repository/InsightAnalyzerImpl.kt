package com.subin.leafy.data.repository

import com.subin.leafy.domain.model.*
import com.subin.leafy.domain.repository.InsightAnalyzer

/**
 * 인사이트 분석 로직 구현체
 * TODO: 나중에 Room DB에서 쿼리로 데이터를 가져와 분석하는 로직으로 확장 가능
 */
class InsightAnalyzerImpl : InsightAnalyzer {

    override fun analyzeTimePattern(notes: List<BrewingNote>): BrewingInsight {
        // [Room DB 포인트] SQL의 GROUP BY를 사용하면 DB 레벨에서 더 빠르게 계산 가능합니다.
        // 현재는 임시로 첫 번째 노트의 시간을 기준으로 생성
        return BrewingInsight(
            id = "time_01",
            type = InsightCategory.TIME_PATTERN,
            emoji = "⏰",
            title = "오후의 티타임",
            description = "사용자님은 주로 오후 3시에 차를 가장 많이 즐기시네요!",
            content = InsightContent.Summary(value = "오후 3시", subValue = "가장 평온한 시간"),
            action = InsightAction.RecordToday
        )
    }

    override fun analyzeTeaPreference(notes: List<BrewingNote>): BrewingInsight {
        // [Room DB 포인트] "SELECT teaType, COUNT(*) FROM notes GROUP BY teaType" 쿼리 결과 대응
        return BrewingInsight(
            id = "pref_01",
            type = InsightCategory.TEA_PREFERENCE,
            emoji = "🍵",
            title = "우롱차 마니아",
            description = "기록된 차 중 60%가 우롱차입니다. 깊은 향을 선호하시나 봐요!",
            content = InsightContent.ChartData(
                labels = listOf("우롱차", "홍차", "녹차", "기타"),
                values = listOf(60f, 20f, 10f, 10f),
                unit = "%"
            ),
            action = InsightAction.GoToWishlist
        )
    }

    override fun analyzePerfectBrewing(notes: List<BrewingNote>): BrewingInsight {
        return BrewingInsight(
            id = "brew_01",
            type = InsightCategory.BREWING_MASTER,
            emoji = "✨",
            title = "황금 비율 발견",
            description = "별점 5점을 주셨던 홍차는 95도에서 3분간 우렸을 때였습니다.",
            content = InsightContent.Summary(value = "95°C / 3분", subValue = "나만의 최적 조건"),
            action = InsightAction.SuggestBrewing(temp = 95, timeSeconds = 180)
        )
    }

    override fun analyzeWellness(notes: List<BrewingNote>): BrewingInsight {
        // [API 포인트] 여기서 나중에 식품 성분 API의 비타민C나 카페인 데이터를 조합합니다.
        return BrewingInsight(
            id = "well_01",
            type = InsightCategory.WELLNESS_REPORT,
            emoji = "🌿",
            title = "오늘의 수분 보충",
            description = "오늘 차를 통해 약 800ml의 수분을 보충하셨습니다. 건강한 습관이에요!",
            content = InsightContent.Recommendation(
                tags = listOf("수분 보충", "디톡스"),
                message = "카페인이 없는 루이보스 차로 하루를 마무리해보는건 어떨까요?"
            ),
            action = InsightAction.OpenFullAnalytics
        )
    }
}