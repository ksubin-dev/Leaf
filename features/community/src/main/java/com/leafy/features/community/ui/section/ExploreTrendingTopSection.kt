package com.leafy.features.community.ui.section

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.ui.component.ExploreNoteUi
import com.leafy.features.community.ui.component.ExploreSectionHeader
import com.leafy.features.community.ui.component.ExploreSummaryNoteCard
import com.leafy.shared.ui.theme.LeafyTheme

/**
 * Explore - Trending 탭
 * "이번 주 인기 노트" 섹션 (제목 + 더보기 + 가로 스크롤 카드 리스트)
 */
@Composable
fun ExploreTrendingTopSection(
    modifier: Modifier = Modifier,
    notes: List<ExploreNoteUi>,
    onNoteClick: (ExploreNoteUi) -> Unit = {}
) {
    Column(modifier = modifier) {

        ExploreSectionHeader(
            title = "이번 주 인기 노트",
            showMore = true,
            onMoreClick = { /* TODO: 인기 전체 보기 이동 */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = notes,
                key = { it.id }
            ) { note ->
                ExploreSummaryNoteCard(
                    note = note,
                    onClick = { onNoteClick(note) },
                    showHotBadge = false,
                    hotLabel = "인기"
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExploreTrendingTopSectionPreview() {
    LeafyTheme {
        val dummyNotes = listOf(
            ExploreNoteUi(
                id = "top_1",
                title = "프리미엄 제주 녹차",
                subtitle = "깔끔하고 상쾌한 맛의 일품",
                imageUrl = null,
                rating = 4.8f,
                authorProfileUrl = null
            ),
            ExploreNoteUi(
                id = "top_2",
                title = "다즐링 퍼스트 플러시",
                subtitle = "인도 | 홍차",
                imageUrl = null,
                rating = 4.6f,
                authorProfileUrl = null
            )
        )

        ExploreTrendingTopSection(
            modifier = Modifier.padding(vertical = 16.dp),
            notes = dummyNotes
        )
    }
}