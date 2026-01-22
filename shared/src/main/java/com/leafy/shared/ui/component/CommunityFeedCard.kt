package com.leafy.shared.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.leafy.shared.ui.model.CommunityPostUiModel
import com.leafy.shared.common.clickableSingle
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.R as SharedR

@Composable
fun CommunityFeedCard(
    post: CommunityPostUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLikeClick: () -> Unit = {},
    onCommentClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {},
    onProfileClick: (String) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickableSingle(onClick = onClick),
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
                LeafyProfileImage(
                    imageUrl = post.authorProfileUrl,
                    size = 36.dp,
                    onClick = { onProfileClick(post.authorId) }
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f).clickableSingle { onProfileClick(post.authorId) }) {
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

                IconButton(onClick = singleClick { /* 더보기 */ }) {
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
                        model = ImageRequest.Builder(context)
                            .data(post.imageUrls.first())
                            .crossfade(true)
                            .build(),
                        contentDescription = post.title,
                        placeholder = painterResource(SharedR.drawable.ic_sample_tea_1),
                        error = painterResource(SharedR.drawable.ic_sample_tea_1),
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )

                    if (post is CommunityPostUiModel.BrewingNote) {
                        Surface(
                            modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = colors.secondary.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = post.teaType,
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
                overflow = TextOverflow.Ellipsis
            )

            if (post is CommunityPostUiModel.BrewingNote) {
                Spacer(modifier = Modifier.height(12.dp))
                if (post.brewingChips.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        post.brewingChips.forEach { chip -> BrewingInfoChip(text = chip) }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingStars(rating = post.rating, size = 16.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.rating}.0",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.tertiary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionButton(
                    iconRes = if (post.isLiked) SharedR.drawable.ic_like_filled else SharedR.drawable.ic_like,
                    count = post.likeCount,
                    isActive = post.isLiked,
                    activeColor = colors.error,
                    inactiveColor = colors.onSurfaceVariant,
                    onClick = onLikeClick
                )

                Spacer(modifier = Modifier.width(16.dp))

                ActionButton(
                    iconRes = SharedR.drawable.ic_comment,
                    count = post.commentCount,
                    isActive = false,
                    activeColor = colors.onSurfaceVariant,
                    inactiveColor = colors.onSurfaceVariant,
                    onClick = onCommentClick
                )

                Spacer(modifier = Modifier.width(16.dp))

                ActionButton(
                    iconRes = if (post.isBookmarked) SharedR.drawable.ic_bookmark_filled else SharedR.drawable.ic_bookmark_outline,
                    count = post.bookmarkCount,
                    isActive = post.isBookmarked,
                    activeColor = colors.primary,
                    inactiveColor = colors.onSurfaceVariant,
                    onClick = onBookmarkClick
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "조회 ${post.viewCount}회",
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    iconRes: Int,
    count: String,
    isActive: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickableSingle(onClick = onClick)
            .padding(4.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = if (isActive) activeColor else inactiveColor,
            modifier = Modifier.size(22.dp)
        )
        if (count != "0") {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                color = if (isActive) activeColor else inactiveColor
            )
        }
    }
}

@Composable
private fun BrewingInfoChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
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

            CommunityFeedCard(
                post = CommunityPostUiModel.BrewingNote(
                    postId = "1",
                    authorId = "user1",
                    authorName = "민지",
                    authorProfileUrl = null,
                    isFollowingAuthor = true,
                    title = "동정오룡차 (Dong Ding Oolong)",
                    content = "은은한 꽃향과 부드러운 과일향이 조화롭게 어우러진 오룽차. 목넘김이 매끄럽고 여운이 깁니다.",
                    imageUrls = listOf("sample_url"),
                    tags = listOf("#우롱차"),
                    timeAgo = "2시간 전",
                    teaType = "우롱차",
                    rating = 5,
                    brewingChips = listOf("95℃", "3m", "5g"),
                    likeCount = "23",
                    commentCount = "5",
                    viewCount = "100",
                    bookmarkCount = "10",
                    isLiked = true,
                    isBookmarked = false,
                    originNoteId = "note_1"
                )
            )


            CommunityFeedCard(
                post = CommunityPostUiModel.General(
                    postId = "2",
                    authorId = "user2",
                    authorName = "티러버",
                    authorProfileUrl = null,
                    isFollowingAuthor = false,
                    title = "오늘 날씨랑 딱 어울리는 무이암차",
                    content = "비 오는 날엔 역시 따뜻한 암차가 최고네요. 다들 오늘 어떤 차 드시나요?",
                    imageUrls = listOf("sample_url_2"),
                    tags = listOf("#무이암차", "#일상"),
                    timeAgo = "방금 전",
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