package com.leafy.features.community.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.ui.component.ExploreSectionHeader
import com.leafy.features.community.ui.component.ExploreTeaMasterCard
import com.leafy.features.community.ui.component.ExploreTeaMasterUi
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme

/**
 * Explore - Trending 탭
 * "이번 달 티 마스터 추천" 섹션
 */
@Composable
fun ExploreTrendingTeaMasterSection(
    masters: List<ExploreTeaMasterUi>,
    modifier: Modifier = Modifier,
    onMasterClick: (ExploreTeaMasterUi) -> Unit = {},
    onFollowToggle: (ExploreTeaMasterUi, Boolean) -> Unit = { _, _ -> }
) {
    Column(modifier = modifier) {

        ExploreSectionHeader(
            title = "이번 달 티 마스터 추천",
            showMore = true,
            onMoreClick = { /* TODO: 티 마스터 전체 보기 */ }
        )

        Spacer(modifier = Modifier.height(12.dp))

        masters.forEachIndexed { index, master ->
            ExploreTeaMasterCard(
                master = master,
                modifier = Modifier,
                onClick = { onMasterClick(master) },
                onFollowToggle = { isFollowing ->
                    onFollowToggle(master, isFollowing)
                }
            )

            if (index != masters.lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExploreTrendingTeaMasterSectionPreview() {
    LeafyTheme {
        val dummyMasters = listOf(
            ExploreTeaMasterUi(
                profileImageRes = SharedR.drawable.ic_profile_4,
                name = "그린티 마니아",
                title = "녹차 & 블렌딩 전문가",
                isFollowing = false
            ),
            ExploreTeaMasterUi(
                profileImageRes = SharedR.drawable.ic_profile_5,
                name = "허브티 큐레이터",
                title = "허브티 & 웰니스 컨설턴트",
                isFollowing = false
            )
        )

        ExploreTrendingTeaMasterSection(masters = dummyMasters)
    }
}