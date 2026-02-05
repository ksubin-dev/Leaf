package com.leafy.features.community.presentation.screen.feed.section

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.presentation.components.card.CommunityCompactCard
import com.leafy.shared.ui.model.CommunityPostUiModel
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
            trailingContent = {
                Text(
                    text = "더보기 →",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.clickable(onClick = singleClick { onMoreClick() })
                )
            }
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
