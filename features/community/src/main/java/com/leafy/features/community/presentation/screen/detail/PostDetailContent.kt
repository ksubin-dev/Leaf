package com.leafy.features.community.presentation.screen.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.leafy.shared.ui.model.CommunityPostUiModel
import com.leafy.shared.R
import com.leafy.shared.common.clickableSingle
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafyProfileImage
import com.leafy.shared.ui.component.RatingStars
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun PostDetailContent(
    post: CommunityPostUiModel,
    onLikeClick: () -> Unit,
    onBookmarkClick: () -> Unit,
    onOriginNoteClick: (String) -> Unit,
    onUserProfileClick: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickableSingle { onUserProfileClick(post.authorId) }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LeafyProfileImage(
                imageUrl = post.authorProfileUrl,
                size = 40.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = post.authorName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = post.timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant
                )
            }
        }

        if (post.imageUrls.isNotEmpty()) {
            AsyncImage(
                model = post.imageUrls.first(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

            when (post) {
                is CommunityPostUiModel.BrewingNote -> {

                    post.originNoteId?.let { originId ->
                        OutlinedButton(
                            onClick = singleClick { onOriginNoteClick(originId) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary),
                            border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.5f))
                        ) {
                            Icon(imageVector = Icons.Rounded.Description, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "작성된 원본 시음 노트 보기", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Surface(shape = RoundedCornerShape(4.dp), color = colors.secondaryContainer) {
                        Text(
                            text = post.teaType,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = colors.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                is CommunityPostUiModel.General -> {
                    // 일반 게시글일 때 추가할 레이아웃이 있다면 여기에 작성
                }
            }

            Text(
                text = post.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge,
                color = colors.onSurface.copy(alpha = 0.9f),
                lineHeight = 24.sp
            )

            if (post is CommunityPostUiModel.BrewingNote) {
                Spacer(modifier = Modifier.height(24.dp))

                if (post.brewingChips.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        post.brewingChips.forEach { chip ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, colors.outlineVariant)
                            ) {
                                Text(
                                    text = chip,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingStars(rating = post.rating, size = 18.dp) // 스마트 캐스트 적용
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.rating}.0",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = singleClick { onLikeClick() }) {
                    Icon(
                        painter = painterResource(
                            if (post.isLiked) R.drawable.ic_like_filled else R.drawable.ic_like
                        ),
                        contentDescription = "좋아요",
                        tint = if (post.isLiked) colors.error else colors.onSurfaceVariant
                    )
                }
                Text(
                    text = post.likeCount,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.onSurface
                )

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    painter = painterResource(R.drawable.ic_comment),
                    contentDescription = "댓글",
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(24.dp).padding(2.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = post.commentCount,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.onSurface
                )

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(onClick = singleClick { onBookmarkClick() }) {
                    Icon(
                        painter = painterResource(
                            if (post.isBookmarked) R.drawable.ic_bookmark_filled else R.drawable.ic_bookmark_outline
                        ),
                        contentDescription = "북마크",
                        tint = if (post.isBookmarked) colors.primary else colors.onSurfaceVariant
                    )
                }
                Text(
                    text = post.bookmarkCount,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.onSurface
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "조회 ${post.viewCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PostDetailContentPreview() {
    LeafyTheme {
        PostDetailContent(
            post = CommunityPostUiModel.BrewingNote(
                postId = "1",
                authorId = "user1",
                authorName = "홍차왕자",
                authorProfileUrl = null,
                isFollowingAuthor = false,
                title = "오늘의 티타임",
                content = "향이 정말 좋은 차입니다. 다들 드셔보세요.",
                imageUrls = listOf(),
                tags = listOf("#홍차", "#티타임"),
                originNoteId = "note_123",
                timeAgo = "1시간 전",
                teaType = "홍차",
                rating = 5,
                brewingChips = listOf("95℃", "3m", "5g"),
                likeCount = "10",
                commentCount = "5",
                viewCount = "100",
                bookmarkCount = "3",
                isLiked = true,
                isBookmarked = false
            ),
            onLikeClick = {},
            onBookmarkClick = {},
            onOriginNoteClick = {},
            onUserProfileClick = {}
        )
    }
}