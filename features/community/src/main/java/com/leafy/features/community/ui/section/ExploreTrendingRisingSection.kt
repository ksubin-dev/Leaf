package com.leafy.features.community.ui.section

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.ui.component.ExploreNoteUi
import com.leafy.features.community.ui.component.ExploreRisingNoteCard
import com.leafy.features.community.ui.component.ExploreSectionHeader
import com.leafy.shared.ui.theme.LeafyTheme

/**
 * Explore - Trending 탭
 * "지금 급상승 중" 섹션
 */
@Composable
fun ExploreTrendingRisingSection(
    modifier: Modifier = Modifier,
    notes: List<ExploreNoteUi>,
    onNoteClick: (ExploreNoteUi) -> Unit = {}
) {
    Column(modifier = modifier) {

        ExploreSectionHeader(
            title = "지금 급상승 중",
            showMore = true,
            onMoreClick = { /* TODO: 급상승 전체 보기 이동 */ }
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
                ExploreRisingNoteCard(
                    note = note,
                    onClick = { onNoteClick(note) },
                    showHotBadge = true,
                    hotLabel = "급상승",
                    modifier = Modifier.width(220.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExploreTrendingRisingSectionPreview() {
    LeafyTheme {
        val dummyNotes = listOf(
            ExploreNoteUi(
                id = "1",
                title = "얼그레이 쌉싸름함",
                subtitle = "베르가못 향의 진한 매력",
                imageUrl = null,
                rating = 4.2f,
                authorName = "Alex",
                authorProfileUrl = null,
                likeCount = 35,
                isLiked = true
            ),
            ExploreNoteUi(
                id = "2",
                title = "히비스커스 블렌딩",
                subtitle = "붉은 빛깔, 상큼한 산미",
                imageUrl = null,
                rating = 4.7f,
                authorName = "Jenny",
                authorProfileUrl = null,
                likeCount = 58,
                isLiked = false
            )
        )

        ExploreTrendingRisingSection(notes = dummyNotes)
    }
}