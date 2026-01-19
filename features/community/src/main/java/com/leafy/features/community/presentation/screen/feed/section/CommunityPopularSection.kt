package com.leafy.features.community.presentation.screen.feed.section

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.presentation.screen.feed.CommunityLargeCard
import com.leafy.features.community.presentation.common.model.CommunityPostUiModel
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafySectionHeader
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun CommunityPopularSection(
    modifier: Modifier = Modifier,
    posts: List<CommunityPostUiModel>,
    onPostClick: (CommunityPostUiModel) -> Unit,
    onMoreClick: () -> Unit,
    onProfileClick: (String) -> Unit,
) {
    if (posts.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth()) {
        LeafySectionHeader(
            title = "이번 주 인기 노트",
            showMore = true,
            onMoreClick = singleClick { onMoreClick() }
        )

        Spacer(modifier = Modifier.height(4.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            items(
                items = posts.take(5),
                key = { it.postId }
            ) { post ->
                CommunityLargeCard(
                    post = post,
                    onClick = { onPostClick(post) },
                    onProfileClick = onProfileClick
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun CommunityPopularSectionPreview() {
    LeafyTheme {
        val samplePosts = listOf(
            CommunityPostUiModel(
                postId = "1",
                authorId = "user1",
                authorName = "녹차마니아",
                authorProfileUrl = null,
                isFollowingAuthor = false,
                title = "프리미엄 제주 녹차",
                content = "깔끔하고 상쾌한 맛이 일품입니다. 아침에 마시기 딱 좋아요.",
                imageUrls = emptyList(),
                timeAgo = "방금 전",
                brewingSummary = "80℃ · 2분",
                teaType = "녹차",
                likeCount = "120",
                commentCount = "45",
                viewCount = "500",
                bookmarkCount = "300",
                rating = 5,
                isLiked = true,
                isBookmarked = false
            ),
            CommunityPostUiModel(
                postId = "2",
                authorId = "user2",
                authorName = "홍차왕자",
                authorProfileUrl = null,
                isFollowingAuthor = true,
                title = "얼그레이 하이티",
                content = "베르가못 향이 아주 진해요.",
                imageUrls = emptyList(),
                timeAgo = "1시간 전",
                brewingSummary = "95℃ · 3분",
                teaType = "홍차",
                likeCount = "80",
                commentCount = "20",
                viewCount = "300",
                bookmarkCount = "150",
                rating = null,
                isLiked = false,
                isBookmarked = true
            )
        )

        CommunityPopularSection(
            posts = samplePosts,
            onPostClick = {},
            onMoreClick = {},
            modifier = Modifier.padding(vertical = 16.dp),
            onProfileClick = { },
        )
    }
}