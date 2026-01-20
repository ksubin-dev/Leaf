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
import com.leafy.shared.ui.model.CommunityPostUiModel
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

