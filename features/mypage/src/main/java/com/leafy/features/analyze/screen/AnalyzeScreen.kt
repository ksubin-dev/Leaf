package com.leafy.features.analyze.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.analyze.data.BrewingPatternData
import com.leafy.features.analyze.data.TeaRecommendation
import com.leafy.features.analyze.ui.components.BrewingPatternSection
import com.leafy.features.analyze.data.TeaTypeRecord
import com.leafy.features.analyze.data.TopTeaRanking
import com.leafy.features.analyze.ui.components.RecommendationSection
import com.leafy.features.analyze.ui.components.TeaTypeRecordSection
import com.leafy.features.analyze.ui.components.TopTeaRankingSection
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun AnalyzeScreen(
    modifier: Modifier = Modifier,
    brewingData: BrewingPatternData,
    teaTypeRecords: List<TeaTypeRecord>,
    recommendations: List<TeaRecommendation>,
    topTeas: List<TopTeaRanking>
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)
    ) {

        BrewingPatternSection(
            data = brewingData,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(24.dp))


        TeaTypeRecordSection(
            records = teaTypeRecords,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(24.dp))


        RecommendationSection(
            recommendations = recommendations,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(24.dp))


        TopTeaRankingSection(
            rankings = topTeas,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(60.dp))
    }
}

// -----------------------------------------------------------
// Preview를 위한 가짜(Dummy) 데이터 (순수한 데이터 사용)
// -----------------------------------------------------------

@Preview(showBackground = true)
@Composable
private fun AnalyzeScreenPreview() {

    val dummyTeaRecords = listOf(
        TeaTypeRecord("녹차", 28),
        TeaTypeRecord("홍차", 35),
        TeaTypeRecord("우롱차", 18),
        TeaTypeRecord("백차", 12),
        TeaTypeRecord("말차", 5),
        TeaTypeRecord("황차", 2)
    )

    val dummyRecommendations = listOf(
        TeaRecommendation("1", "Dragon Well Green", "Tea lover", 4.5f, "비슷한 취향", ""),
        TeaRecommendation("2", "Iron Goddess Oolong", "Teavana", 4.7f, "새로운 발견", "")
    )

    val dummyTopTeas = listOf(
        TopTeaRanking(1, "Milky Oolong", 12, 4.8f, ""),
        TopTeaRanking(2, "Chamomile Blend", 9, 4.7f, ""),
        TopTeaRanking(3, "Darjeeling First Flush", 7, 4.6f, "")
    )

    LeafyTheme {
        AnalyzeScreen(
            modifier = Modifier.fillMaxSize(),
            brewingData = BrewingPatternData("85°C", "3분 30초", "4회", "오후 (14:00 - 17:00)"),
            teaTypeRecords = dummyTeaRecords,
            recommendations = dummyRecommendations,
            topTeas = dummyTopTeas
        )
    }
}