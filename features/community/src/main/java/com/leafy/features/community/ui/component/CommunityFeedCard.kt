package com.leafy.features.community.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.leafy.features.community.ui.model.CommunityPostUiModel
import com.leafy.shared.ui.component.RatingStars
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.R as SharedR

@Composable
fun CommunityFeedCard(
    post: CommunityPostUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = post.authorProfileUrl,
                    contentDescription = post.authorName,
                    placeholder = painterResource(SharedR.drawable.ic_profile_1),
                    error = painterResource(SharedR.drawable.ic_profile_1),
                    modifier = Modifier.size(36.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.authorName,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.onSurface
                    )
                    Text(
                        text = post.timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.onSurfaceVariant
                    )
                }

                IconButton(onClick = { /* 더보기 메뉴 (신고 등) */ }) {
                    Icon(
                        painter = painterResource(id = SharedR.drawable.ic_more_vert),
                        contentDescription = "More",
                        tint = colors.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (post.imageUrls.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    AsyncImage(
                        model = post.imageUrls.first(),
                        contentDescription = post.title,
                        placeholder = painterResource(SharedR.drawable.ic_sample_tea_1),
                        error = painterResource(SharedR.drawable.ic_sample_tea_1),
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                    
                    if (post.isBrewingNote) {
                        Surface(
                            modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = colors.secondary.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = post.teaType ?: "Tea",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                color = colors.onSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
                maxLines = 3,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            if (post.isBrewingNote) {
                Spacer(modifier = Modifier.height(12.dp))

                if (post.brewingChips.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        post.brewingChips.forEach { chip ->
                            BrewingInfoChip(text = chip)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                post.rating?.let { rating ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RatingStars(rating = rating, size = 16.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$rating.0",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.tertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        painter = painterResource(
                            id = if (post.isLiked) SharedR.drawable.ic_like_filled else SharedR.drawable.ic_like
                        ),
                        contentDescription = "Like",
                        tint = if (post.isLiked) colors.error else colors.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(onClick = onCommentClick) {
                    Icon(
                        painter = painterResource(id = SharedR.drawable.ic_comment),
                        contentDescription = "Comment",
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = onBookmarkClick) {
                    Icon(
                        painter = painterResource(
                            id = if (post.isBookmarked) SharedR.drawable.ic_bookmark_filled else SharedR.drawable.ic_bookmark_outline
                        ),
                        contentDescription = "Bookmark",
                        tint = if (post.isBookmarked) colors.primary else colors.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            if (post.likeCount != "0") {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${post.likeCount}명이 좋아합니다",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.onSurface,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun BrewingInfoChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = null // 깔끔하게
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityFeedCardPreview() {
    LeafyTheme {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. 브루잉 노트 공유 예시
            CommunityFeedCard(
                post = CommunityPostUiModel(
                    postId = "1",
                    authorId = "user1",
                    authorName = "민지",
                    authorProfileUrl = null,
                    isFollowingAuthor = true,
                    title = "동정오룡차 (Dong Ding Oolong)",
                    content = "은은한 꽃향과 부드러운 과일향이 조화롭게 어우러진 오룽차. 목넘김이 매끄럽고 여운이 깁니다.",
                    imageUrls = listOf("sample_url"),
                    timeAgo = "2시간 전",
                    teaType = "우롱차",
                    brewingSummary = "95℃ · 3m · 5g",
                    rating = 5,
                    likeCount = "23",
                    commentCount = "5",
                    viewCount = "100",
                    bookmarkCount = "10",
                    isLiked = true,
                    isBookmarked = false
                )
            )

            // 2. 일반 수다 글 예시
            CommunityFeedCard(
                post = CommunityPostUiModel(
                    postId = "2",
                    authorId = "user2",
                    authorName = "티러버",
                    authorProfileUrl = null,
                    isFollowingAuthor = false,
                    title = "오늘 날씨랑 딱 어울리는 무이암차",
                    content = "비 오는 날엔 역시 따뜻한 암차가 최고네요. 다들 오늘 어떤 차 드시나요?",
                    imageUrls = listOf("sample_url_2"),
                    timeAgo = "방금 전",
                    teaType = null,
                    brewingSummary = null,
                    rating = null,
                    likeCount = "124",
                    commentCount = "18",
                    viewCount = "300",
                    bookmarkCount = "45",
                    isLiked = false,
                    isBookmarked = true
                )
            )
        }
    }
}