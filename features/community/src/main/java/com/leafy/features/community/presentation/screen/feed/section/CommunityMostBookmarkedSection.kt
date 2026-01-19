package com.leafy.features.community.presentation.screen.feed.section

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.presentation.components.card.CommunityCompactCard
import com.leafy.features.community.presentation.common.model.CommunityPostUiModel
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafySectionHeader
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun CommunityMostBookmarkedSection(
    modifier: Modifier = Modifier,
    posts: List<CommunityPostUiModel>,
    onPostClick: (CommunityPostUiModel) -> Unit,
    onBookmarkClick: (CommunityPostUiModel) -> Unit,
    onMoreClick: () -> Unit
) {
    if (posts.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        LeafySectionHeader(
            title = "명예의 전당",
            onMoreClick = singleClick { onMoreClick() }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            posts.take(3).forEach { post ->
                CommunityCompactCard(
                    post = post,
                    onClick = { onPostClick(post) },
                    onBookmarkClick = { onBookmarkClick(post) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityMostBookmarkedSectionPreview() {
    LeafyTheme {
        val samplePosts = listOf(
            CommunityPostUiModel(
                postId = "1",
                authorId = "user1",
                authorName = "티러버",
                authorProfileUrl = null,
                isFollowingAuthor = false,
                title = "다즐링 퍼스트 플러시",
                content = "내용",
                imageUrls = emptyList(),
                timeAgo = "1시간 전",
                brewingSummary = "95℃ · 3분",
                teaType = "홍차",
                likeCount = "50",
                commentCount = "12",
                viewCount = "1.5k",
                bookmarkCount = "1.2k",
                rating = 5,
                isLiked = false,
                isBookmarked = true
            ),
            CommunityPostUiModel(
                postId = "2",
                authorId = "user2",
                authorName = "차마스터",
                authorProfileUrl = null,
                isFollowingAuthor = true,
                title = "백모단 화이트티",
                content = "내용",
                imageUrls = emptyList(),
                timeAgo = "2시간 전",
                brewingSummary = "80℃ · 2분",
                teaType = "백차",
                likeCount = "30",
                commentCount = "5",
                viewCount = "1.0k",
                bookmarkCount = "987",
                rating = 4,
                isLiked = true,
                isBookmarked = true
            ),
            CommunityPostUiModel(
                postId = "3",
                authorId = "user3",
                authorName = "허브향기",
                authorProfileUrl = null,
                isFollowingAuthor = false,
                title = "루이보스 바닐라",
                content = "내용",
                imageUrls = emptyList(),
                timeAgo = "3시간 전",
                brewingSummary = "100℃ · 5분",
                teaType = "허브티",
                likeCount = "45",
                commentCount = "8",
                viewCount = "900",
                bookmarkCount = "854",
                rating = null,
                isLiked = false,
                isBookmarked = false
            )
        )

        CommunityMostBookmarkedSection(
            posts = samplePosts,
            onPostClick = {},
            onBookmarkClick = {},
            onMoreClick = {},
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}