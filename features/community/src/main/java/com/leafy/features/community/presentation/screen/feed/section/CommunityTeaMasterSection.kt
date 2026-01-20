package com.leafy.features.community.presentation.screen.feed.section

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.component.CommunityTeaMasterCard
import com.leafy.shared.ui.model.UserUiModel
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafySectionHeader
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun CommunityTeaMasterSection(
    modifier: Modifier = Modifier,
    currentUserId: String?,
    masters: List<UserUiModel>,
    onMasterClick: (UserUiModel) -> Unit,
    onFollowToggle: (UserUiModel) -> Unit,
    onMoreClick: () -> Unit
) {
    if (masters.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        LeafySectionHeader(
            title = "이번 달 티 마스터 추천",
            showMore = true,
            onMoreClick = singleClick { onMoreClick() }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            masters.take(3).forEach { master ->
                CommunityTeaMasterCard(
                    master = master,
                    currentUserId = currentUserId,
                    onClick = { onMasterClick(master) },
                    onFollowToggle = { onFollowToggle(master) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityTeaMasterSectionPreview() {
    LeafyTheme {
        val dummyMasters = listOf(
            UserUiModel(
                userId = "m1",
                nickname = "티마스터 소영",
                title = "홍차 전문 테이스터",
                profileImageUrl = null,
                isFollowing = false,
                followerCount = "1.2k",
                expertTags = listOf("홍차")
            ),
            UserUiModel(
                userId = "m2",
                nickname = "그린티 마니아",
                title = "녹차 & 말차 전문가",
                profileImageUrl = null,
                isFollowing = true,
                followerCount = "500",
                expertTags = listOf("녹차", "말차")
            ),
            UserUiModel(
                userId = "m3",
                nickname = "허브티 큐레이터",
                title = "허브티 & 웰니스 전문",
                profileImageUrl = null,
                isFollowing = false,
                followerCount = "300",
                expertTags = listOf("허브티")
            )
        )

        CommunityTeaMasterSection(
            masters = dummyMasters,
            currentUserId = "m2",
            onMasterClick = {},
            onFollowToggle = {},
            onMoreClick = {},
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}