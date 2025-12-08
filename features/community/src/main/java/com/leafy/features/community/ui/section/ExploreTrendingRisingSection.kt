package com.leafy.features.community.ui.section

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.ui.component.ExploreNoteSummaryUi
import com.leafy.features.community.ui.component.ExploreRisingNoteCard // 💡 Import 변경
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

        // 🔹 가로 스크롤 카드 리스트 (Rising 카드 사용)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notes) { note ->
                // 💡 ExploreRisingNoteCard 사용
                ExploreRisingNoteCard(
                    note = note,
                    onClick = { onNoteClick(note) },
                    showHotBadge = true, // '급상승' 뱃지 표시
                    hotLabel = "급상승"
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
            // 급상승 노트는 작성자/좋아요 정보가 필수적으로 포함되도록 데이터를 설정합니다.
            ExploreNoteSummaryUi(
                title = "얼그레이 쌉싸름함",
                subtitle = "베르가못 향의 진한 매력",
                imageRes = SharedR.drawable.ic_sample_tea_3,
                rating = 4.2f,
                savedCount = 98,
                profileImageRes = SharedR.drawable.ic_profile_1, // 프로필 정보
                authorName = "Alex",
                likeCount = 35, // 좋아요 정보
                isLiked = true
            ),
            ExploreNoteSummaryUi(
                title = "히비스커스 블렌딩",
                subtitle = "붉은 빛깔, 상큼한 산미",
                imageRes = SharedR.drawable.ic_sample_tea_4,
                rating = 4.7f,
                savedCount = 120,
                profileImageRes = SharedR.drawable.ic_profile_2,
                authorName = "Jenny",
                likeCount = 58,
                isLiked = false
            ),
            ExploreNoteSummaryUi(
                title = "아쌈 강한 바디감",
                subtitle = "밀크티에 완벽한 베이스",
                imageRes = SharedR.drawable.ic_sample_tea_1,
                rating = 4.4f,
                savedCount = 76,
                profileImageRes = SharedR.drawable.ic_profile_3,
                authorName = "Peter",
                likeCount = 29,
                isLiked = true
            )
        )

        ExploreTrendingRisingSection(notes = dummyNotes)
    }
}