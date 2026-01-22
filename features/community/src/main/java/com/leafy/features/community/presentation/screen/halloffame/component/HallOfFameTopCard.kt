package com.leafy.features.community.presentation.screen.halloffame.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.leafy.shared.ui.model.CommunityPostUiModel
import com.leafy.shared.common.clickableSingle
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.R as SharedR

@Composable
fun HallOfFameTopCard(
    post: CommunityPostUiModel,
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clickableSingle(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = post.imageUrls.firstOrNull(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                placeholder = painterResource(id = SharedR.drawable.ic_sample_tea_1),
                error = painterResource(id = SharedR.drawable.ic_sample_tea_1)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF212121).copy(alpha = 0.3f),
                                Color(0xFF212121).copy(alpha = 0.9f)
                            )
                        )
                    )
            )

            Surface(
                color = colors.primary,
                shape = RoundedCornerShape(bottomEnd = 16.dp),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = "1st Rank",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "MASTERPIECE",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (post is CommunityPostUiModel.BrewingNote) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = post.teaType,
                            color = colors.primaryContainer,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "\"${post.title}\"",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    ),
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "by ${post.authorName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.outlineVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (post.isBookmarked) SharedR.drawable.ic_bookmark_filled
                            else SharedR.drawable.ic_bookmark_outline
                        ),
                        contentDescription = "Bookmark",
                        tint = if (post.isBookmarked) colors.primary else Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickableSingle { onBookmarkClick() }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = post.bookmarkCount, color = Color.White, fontWeight = FontWeight.SemiBold)

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(
                        painter = painterResource(id = SharedR.drawable.ic_comment),
                        contentDescription = "Comments",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = post.commentCount, color = Color.White)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "1등 카드 (기본)")
@Composable
private fun HallOfFameTopCardPreview() {
    LeafyTheme {
        val dummyPost = CommunityPostUiModel.BrewingNote(
            postId = "1",
            authorId = "user1",
            authorName = "티마스터_연우",
            authorProfileUrl = null,
            isFollowingAuthor = false,
            title = "동양차의 깊이, 무이암차의 모든 것",
            content = "내용",
            imageUrls = listOf("https://via.placeholder.com/600x800"),
            tags = listOf("#무이암차"),
            timeAgo = "2시간 전",
            teaType = "무이암차",
            rating = 5,
            brewingChips = listOf("95℃", "3m"),
            likeCount = "1.2k",
            commentCount = "84",
            viewCount = "5000",
            bookmarkCount = "1.2k",
            isLiked = false,
            isBookmarked = false,
            originNoteId = null
        )

        Box(modifier = Modifier.padding(16.dp)) {
            HallOfFameTopCard(
                post = dummyPost,
                onClick = {},
                onBookmarkClick = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "1등 카드 (북마크됨)")
@Composable
private fun HallOfFameTopCardBookmarkedPreview() {
    LeafyTheme {
        val dummyPost = CommunityPostUiModel.General(
            postId = "1",
            authorId = "user1",
            authorName = "그린티홀릭",
            authorProfileUrl = null,
            isFollowingAuthor = true,
            title = "실패 없는 말차 격불의 정석 (영상 포함)",
            content = "내용",
            imageUrls = emptyList(),
            tags = listOf("#말차"),
            timeAgo = "1일 전",
            likeCount = "856",
            commentCount = "120",
            viewCount = "3000",
            bookmarkCount = "856",
            isLiked = true,
            isBookmarked = true
        )

        Box(modifier = Modifier.padding(16.dp)) {
            HallOfFameTopCard(
                post = dummyPost,
                onClick = {},
                onBookmarkClick = {}
            )
        }
    }
}