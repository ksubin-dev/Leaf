package com.leafy.features.community.ui.section

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.ui.component.ExploreTeaMasterCard
import com.leafy.features.community.ui.component.ExploreTeaMasterUi
import com.leafy.shared.ui.component.LeafySectionHeader
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun ExploreTrendingTeaMasterSection(
    modifier: Modifier = Modifier,
    masters: List<ExploreTeaMasterUi>,
    onMasterClick: (ExploreTeaMasterUi) -> Unit = {},
    onFollowToggle: (ExploreTeaMasterUi, Boolean) -> Unit = { _, _ -> },
    onMoreClick: () -> Unit = {}
) {
    Column(modifier = modifier) {
        LeafySectionHeader(
            title = "이번 달 티 마스터 추천",
            titleStyle = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            showMore = true,
            onMoreClick = onMoreClick
        )

        Spacer(modifier = Modifier.height(4.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            masters.forEach { master ->
                ExploreTeaMasterCard(
                    master = master,
                    onClick = { onMasterClick(master) },
                    onFollowToggle = { isFollowing ->
                        onFollowToggle(master, isFollowing)
                    }
                )
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
                id = "m1",
                profileImageUrl = null,
                name = "그린티 마니아",
                title = "녹차 & 블렌딩 전문가",
                isFollowing = false
            ),
            ExploreTeaMasterUi(
                id = "m2",
                profileImageUrl = null,
                name = "허브티 큐레이터",
                title = "허브티 & 웰니스 컨설턴트",
                isFollowing = true
            )
        )

        ExploreTrendingTeaMasterSection(
            modifier = Modifier.padding(16.dp),
            masters = dummyMasters
        )
    }
}