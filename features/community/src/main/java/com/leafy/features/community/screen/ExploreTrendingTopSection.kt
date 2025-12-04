package com.leafy.features.community.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.ui.component.ExploreNoteSmallCard
import com.leafy.features.community.ui.component.ExploreNoteSummaryUi
import com.leafy.features.community.ui.component.ExploreSectionHeader
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.R as SharedR

/**
 * Explore - Trending 탭
 * "이번 주 인기 노트" 섹션 UI
 */
@Composable
fun ExploreTrendingTopSection(
    notes: List<ExploreNoteSummaryUi>,
    modifier: Modifier = Modifier,
    onNoteClick: (ExploreNoteSummaryUi) -> Unit = {}
) {
    Column(modifier = modifier) {

        // 섹션 헤더
        ExploreSectionHeader(
            title = "이번 주 인기 노트",
            onMoreClick = { /* TODO: 전체 보기 */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 가로 스크롤 카드 리스트
        LazyRow(
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            items(notes) { note ->
                ExploreNoteSmallCard(
                    note = note,
                    onClick = { onNoteClick(note) }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun ExploreTrendingTopSectionPreview() {
    LeafyTheme {
        val dummyNotes = listOf(
            ExploreNoteSummaryUi(
                title = "프리미엄 제주 녹차",
                subtitle = "깔끔하고 상쾌한 맛의 일품",
                imageRes = SharedR.drawable.ic_sample_tea_1,
                rating = 4.8f,
                reviewCount = 234,
                profileImageRes = SharedR.drawable.ic_profile_1
            ),
            ExploreNoteSummaryUi(
                title = "다즐링 퍼스트 플러시",
                subtitle = "인도 | 홍차",
                imageRes = SharedR.drawable.ic_sample_tea_2,
                rating = 4.6f,
                reviewCount = 189,
                profileImageRes = SharedR.drawable.ic_profile_2
            ),
            ExploreNoteSummaryUi(
                title = "카모마일 허브티",
                subtitle = "부드러운 꽃향과 허브 향",
                imageRes = SharedR.drawable.ic_sample_tea_3,
                rating = 4.5f,
                reviewCount = 142,
                profileImageRes = SharedR.drawable.ic_profile_3
            )
        )

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            ExploreTrendingTopSection(notes = dummyNotes)
        }
    }
}