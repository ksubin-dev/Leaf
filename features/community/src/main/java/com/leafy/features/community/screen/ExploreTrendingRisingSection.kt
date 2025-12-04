package com.leafy.features.community.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.ui.component.ExploreHotNoteCard
import com.leafy.features.community.ui.component.ExploreNoteSummaryUi
import com.leafy.features.community.ui.component.ExploreSectionHeader
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme

/**
 * Explore - Trending 탭
 * "지금 급상승 중" 섹션 (제목 + 더보기 + 가로 스크롤 카드 리스트)
 */
@Composable
fun ExploreTrendingRisingSection(
    notes: List<ExploreNoteSummaryUi>,
    modifier: Modifier = Modifier,
    onNoteClick: (ExploreNoteSummaryUi) -> Unit = {}
) {
    Column(modifier = modifier) {

        // 🔹 제목 + 더보기
        ExploreSectionHeader(
            title = "지금 급상승 중",
            showMore = true,
            onMoreClick = { /* TODO: 급상승 전체 보기 이동 */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 🔹 가로 스크롤 카드 리스트 (Hot 카드 사용)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notes) { note ->
                ExploreHotNoteCard(
                    note = note,
                    onClick = { onNoteClick(note) }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun ExploreTrendingRisingSectionPreview() {
    LeafyTheme {
        val dummyNotes = listOf(
            ExploreNoteSummaryUi(
                title = "자스민 그린티",
                subtitle = "은은한 꽃향이 매력적",
                imageRes = SharedR.drawable.ic_sample_tea_2,
                rating = 4.7f,
                reviewCount = 120
            ),
            ExploreNoteSummaryUi(
                title = "카모마일 허브티",
                subtitle = "편안한 밤을 위한 한 잔",
                imageRes = SharedR.drawable.ic_sample_tea_3,
                rating = 4.6f,
                reviewCount = 98
            ),
            ExploreNoteSummaryUi(
                title = "루이보스 바닐라",
                subtitle = "부드러운 루이보스 · 허브티",
                imageRes = SharedR.drawable.ic_sample_tea_1,
                rating = 4.5f,
                reviewCount = 87
            )
        )

        ExploreTrendingRisingSection(notes = dummyNotes)
    }
}