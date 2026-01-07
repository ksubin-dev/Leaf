package com.subin.leafy.domain.repository

import com.subin.leafy.domain.model.BrewingInsight
import com.subin.leafy.domain.model.BrewingNote

interface InsightAnalyzer {
    // 1. 시간대별 분석 (언제 가장 많이 마시는가?)
    fun analyzeTimePattern(notes: List<BrewingNote>): BrewingInsight

    // 2. 선호도 분석 (어떤 차 종류를 선호하는가? - 도넛 차트용)
    fun analyzeTeaPreference(notes: List<BrewingNote>): BrewingInsight

    // 3. 브루잉 패턴 분석 (가장 완벽했던 우림 온도/시간은?)
    fun analyzePerfectBrewing(notes: List<BrewingNote>): BrewingInsight

    // 4. 건강 분석 (수분 섭취량 및 예상 카페인 상태)
    fun analyzeWellness(notes: List<BrewingNote>): BrewingInsight
}