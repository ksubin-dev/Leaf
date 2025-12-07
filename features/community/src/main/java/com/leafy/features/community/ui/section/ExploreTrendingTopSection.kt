package com.leafy.features.community.ui.section

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.ui.component.ExploreNoteSummaryUi
import com.leafy.features.community.ui.component.ExploreSectionHeader
import com.leafy.features.community.ui.component.ExploreSummaryNoteCard
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme

/**
 * Explore - Trending 탭
 * "이번 주 인기 노트" 섹션 (제목 + 더보기 + 가로 스크롤 카드 리스트)
 */
@Composable
fun ExploreTrendingTopSection(
    notes: List<ExploreNoteSummaryUi>,
    modifier: Modifier = Modifier,
    onNoteClick: (ExploreNoteSummaryUi) -> Unit = {}
) {
    Column(modifier = modifier) {


        ExploreSectionHeader(
            title = "이번 주 인기 노트",
            showMore = true,
            onMoreClick = { /* TODO: 인기 전체 보기 이동 */ }
        )

        Spacer(modifier = Modifier.height(12.dp))


        LazyRow(
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notes) { note ->
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

@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun ExploreTrendingTopSectionPreview() {
    LeafyTheme {
        val dummyNotes = listOf(
            // 💡 프로필 이미지 정보 추가
            ExploreNoteSummaryUi(
                title = "프리미엄 제주 녹차",
                subtitle = "깔끔하고 상쾌한 맛의 일품",
                imageRes = SharedR.drawable.ic_sample_tea_1,
                rating = 4.8f,
                savedCount = 234,
                profileImageRes = SharedR.drawable.ic_profile_1
            ),
            ExploreNoteSummaryUi(
                title = "다즐링 퍼스트 플러시",
                subtitle = "인도 | 홍차",
                imageRes = SharedR.drawable.ic_sample_tea_2,
                rating = 4.6f,
                savedCount = 189,
                profileImageRes = SharedR.drawable.ic_profile_2
            ),
            ExploreNoteSummaryUi(
                title = "카모마일 허브티",
                subtitle = "부드러운 꽃향과 허브 향",
                imageRes = SharedR.drawable.ic_sample_tea_3,
                rating = 4.5f,
                savedCount = 142,
                profileImageRes = SharedR.drawable.ic_profile_3
            )
        )

        ExploreTrendingTopSection(notes = dummyNotes)
    }
}