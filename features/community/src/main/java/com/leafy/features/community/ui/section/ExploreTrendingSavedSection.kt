package com.leafy.features.community.ui.section

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.ui.component.ExploreSavedNoteCard
import com.leafy.features.community.ui.component.ExploreNoteUi
import com.leafy.features.community.ui.component.ExploreSectionHeader
import com.leafy.shared.ui.theme.LeafyTheme

/**
 * Explore - Trending 탭
 * "가장 많이 저장된 노트" 섹션 (세로 리스트 형태)
 */
@Composable
fun ExploreTrendingSavedSection(
    modifier: Modifier = Modifier,
    notes: List<ExploreNoteUi>,
    onNoteClick: (ExploreNoteUi) -> Unit = {}
) {
    Column(modifier = modifier) {

        ExploreSectionHeader(
            title = "가장 많이 저장된 노트",
            showMore = true,
            onMoreClick = { /* TODO: 전체 보기 페이지 이동 */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

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
            ExploreNoteUi(
                id = "1",
                title = "다즐링 퍼스트 플러시",
                subtitle = "인도 | 홍차",
                imageUrl = null,
                rating = 4.1f,
                savedCount = 1200
            ),
            ExploreNoteUi(
                id = "2",
                title = "백모단 화이트티",
                subtitle = "중국 | 백차",
                imageUrl = null,
                rating = 4.3f,
                savedCount = 987
            ),
            ExploreNoteUi(
                id = "3",
                title = "루이보스 바닐라",
                subtitle = "남아공 | 허브티",
                imageUrl = null,
                rating = 4.2f,
                savedCount = 854
            ),
        )
        ExploreTrendingSavedSection(
            modifier = Modifier.padding(16.dp),
            notes = sample
        )
    }
}