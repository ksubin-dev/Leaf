package com.leafy.features.community.presentation.screen.profile.section

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.community.presentation.common.model.CommunityPostUiModel
import com.leafy.shared.common.clickableSingle
import com.leafy.shared.ui.component.RatingStars
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun UserProfileList(
    posts: List<CommunityPostUiModel>,
    onPostClick: (String) -> Unit
) {
    if (posts.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "아직 작성한 기록이 없습니다.",
                color = Color.Gray
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(posts) { post ->
                ProfileListCard(
                    post = post,
                    onClick = { onPostClick(post.postId) }
                )
            }
        }
    }
}

@Composable
private fun ProfileListCard(
    post: CommunityPostUiModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickableSingle { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                if (post.rating != null && post.rating > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    RatingStars(
                        rating = post.rating,
                        size = 16.dp,
                        filledColor = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = post.timeAgo,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (post.content.isNotBlank()) {
                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (!post.brewingSummary.isNullOrBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = post.brewingSummary,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F5F5)
@Composable
private fun UserProfileListPreview() {
    val dummyPosts = listOf(
        CommunityPostUiModel(
            postId = "1", authorId = "u1", authorName = "루시", authorProfileUrl = null,
            isFollowingAuthor = false, title = "[무이암차] 수금귀",
            content = "암운이 아주 강렬하게 느껴지는 날이었습니다. 첫 번째 포에서는 나무향이 지배적이었지만 뒤로 갈수록...",
            imageUrls = emptyList(), timeAgo = "2023.10.24",
            teaType = "OOLONG", brewingSummary = "95℃ · 30s · 5g", rating = 5, // 별점 5점
            likeCount = "10", commentCount = "2", viewCount = "100", bookmarkCount = "5",
            isLiked = false, isBookmarked = false
        ),
        CommunityPostUiModel(
            postId = "2", authorId = "u1", authorName = "루시", authorProfileUrl = null,
            isFollowingAuthor = false, title = "[봉황단총] 밀란향",
            content = "향이 정말 폭발적입니다. 과일 향이 가득하네요.",
            imageUrls = emptyList(), timeAgo = "2일 전",
            teaType = "OOLONG", brewingSummary = "90℃ · 20s · 4g", rating = 4,
            likeCount = "5", commentCount = "0", viewCount = "50", bookmarkCount = "1",
            isLiked = true, isBookmarked = false
        ),
        CommunityPostUiModel(
            postId = "3", authorId = "u1", authorName = "루시", authorProfileUrl = null,
            isFollowingAuthor = false, title = "오늘의 차",
            content = "비 오는 날 마시는 차가 제일 맛있네요.",
            imageUrls = emptyList(), timeAgo = "방금 전",
            teaType = null, brewingSummary = null, rating = null,
            likeCount = "1", commentCount = "0", viewCount = "10", bookmarkCount = "0",
            isLiked = false, isBookmarked = false
        )
    )

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LeafyTheme {
            UserProfileList(
                posts = dummyPosts,
                onPostClick = {}
            )
        }
    }
}