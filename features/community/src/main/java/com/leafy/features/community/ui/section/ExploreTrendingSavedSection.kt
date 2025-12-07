package com.leafy.features.community.ui.section

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.ui.component.ExploreSavedNoteCard
import com.leafy.features.community.ui.component.ExploreNoteSummaryUi
import com.leafy.features.community.ui.component.ExploreSectionHeader
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme

/**
 * Explore - Trending 탭
 * "가장 많이 저장된 노트" 섹션
 */
@Composable
fun ExploreTrendingSavedSection(
    notes: List<ExploreNoteSummaryUi>,
    modifier: Modifier = Modifier,
    onNoteClick: (ExploreNoteSummaryUi) -> Unit = {}
) {
    Column(modifier = modifier) {

        ExploreSectionHeader(
            title = "가장 많이 저장된 노트",
            showMore = true,
            onMoreClick = { /* TODO */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        //LazyColumn 대신 Column 사용
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            notes.forEach { note ->
                ExploreSavedNoteCard(
                    note = note,
                    onClick = { onNoteClick(note) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExploreTrendingSavedSectionPreview() {
    LeafyTheme {
        val sample = listOf(
            ExploreNoteSummaryUi(
                title = "다즐링 퍼스트 플러시",
                subtitle = "인도 | 홍차",
                imageRes = SharedR.drawable.ic_sample_tea_4,
                rating = 4.1f,
                savedCount = 1200
            ),
            ExploreNoteSummaryUi(
                title = "백모단 화이트티",
                subtitle = "중국 | 백차",
                imageRes = SharedR.drawable.ic_sample_tea_5,
                rating = 4.3f,
                savedCount = 987
            ),
            ExploreNoteSummaryUi(
                title = "루이보스 바닐라",
                subtitle = "남아공 | 허브티",
                imageRes = SharedR.drawable.ic_sample_tea_6,
                rating = 4.2f,
                savedCount = 854
            ),
        )
        ExploreTrendingSavedSection(sample, modifier = Modifier.padding(16.dp))
    }
}
