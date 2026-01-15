package com.leafy.features.community.ui.feed.section


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leafy.features.community.ui.component.CommunityFeedCard
import com.leafy.features.community.ui.model.CommunityPostUiModel

@Composable
fun CommunityFollowingFeedSection(
    modifier: Modifier = Modifier,
    posts: List<CommunityPostUiModel>,
    onPostClick: (CommunityPostUiModel) -> Unit,
    onLikeClick: (CommunityPostUiModel) -> Unit,
    onCommentClick: (CommunityPostUiModel) -> Unit,
    onBookmarkClick: (CommunityPostUiModel) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(
            items = posts,
            key = { it.postId }
        ) { post ->
            CommunityFeedCard(
                post = post,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                onClick = { onPostClick(post) },
                onLikeClick = { onLikeClick(post) },
                onCommentClick = { onCommentClick(post) },
                onBookmarkClick = { onBookmarkClick(post) }
            )
        }
    }
}