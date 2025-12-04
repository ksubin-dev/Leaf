package com.leafy.features.community.ui.component

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme

/**
 * "지금 급상승 중" 카드
 * - ExploreNoteSmallCard를 재사용
 * - 프로필은 숨기고, 상단에 '급상승' 뱃지만 붙임
 */
@Composable
fun ExploreHotNoteCard(
    note: ExploreNoteSummaryUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    ExploreNoteSmallCard(
        note = note,
        modifier = modifier,
        onClick = onClick,
        showProfile = false,      // 프로필 숨김
        showHotBadge = true,      // 급상 뱃지 켜기
        hotLabel = "급상승"
    )
}

@Preview(showBackground = true)
@Composable
private fun ExploreHotNoteCardPreview() {
    LeafyTheme {
        ExploreHotNoteCard(
            note = ExploreNoteSummaryUi(
                title = "자스민 그린티",
                subtitle = "은은한 꽃향이 매력적",
                imageRes = SharedR.drawable.ic_sample_tea_2,
                rating = 4.7f,
                reviewCount = 120,
            ),
            modifier = Modifier.width(170.dp)
        )
    }
}