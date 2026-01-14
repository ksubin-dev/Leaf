package com.leafy.features.community.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.R as SharedR

/**
 * 지금 급상승 중 카드 (작성자 및 좋아요 정보 포함)
 */
@Composable
fun ExploreRisingNoteCard(
    note: ExploreNoteSummaryUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    showHotBadge: Boolean = true,
    hotLabel: String = "급상승"
) {
    val colors = MaterialTheme.colorScheme
    val profileRes = note.profileImageRes ?: SharedR.drawable.ic_profile_3
    val authorName = note.authorName ?: "Unknown"
    val likeCount = note.likeCount ?: 0

    Card(
        modifier = modifier
            .width(220.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Image(
                    painter = painterResource(id = note.imageRes),
                    contentDescription = note.title,
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )

                if (showHotBadge) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(colors.error)
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = hotLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.onPrimary
                        )
                    }
                }
            }

            // 텍스트 + 별점 + 하단 정보 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = colors.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val filledStars = note.rating.toInt().coerceIn(0, 5)
                    val emptyStars = 5 - filledStars

                    // 채워진 별점 (★)
                    Text(
                        text = "★".repeat(filledStars),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.error
                    )
                    Text(
                        text = "★".repeat(emptyStars),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.error.copy(alpha = 0.3f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = note.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = profileRes),
                            contentDescription = authorName,
                            modifier = Modifier
                                .size(30.dp) 
                                .clip(RoundedCornerShape(50)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = authorName,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(
                                id = if (note.isLiked) SharedR.drawable.ic_like_filled else SharedR.drawable.ic_like
                            ),
                            contentDescription = "Like count",
                            tint = if (note.isLiked) colors.error else colors.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp).clickable { /* TODO: 좋아요 클릭 이벤트 */ }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = likeCount.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExploreRisingNoteCardPreview() {
    LeafyTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(16.dp)) {
            ExploreRisingNoteCard(
                note = ExploreNoteSummaryUi(
                    title = "Chamomile Dreams",
                    subtitle = "Soothing, honey-like sweetness...",
                    imageRes = SharedR.drawable.ic_sample_tea_1,
                    rating = 4.0f,
                    savedCount = 50,
                    profileImageRes = SharedR.drawable.ic_profile_1,
                    authorName = "Sarah",
                    likeCount = 12,
                    isLiked = false
                ),
                modifier = Modifier.width(170.dp)
            )
            ExploreRisingNoteCard(
                note = ExploreNoteSummaryUi(
                    title = "Jasmine Phoenix Pearls",
                    subtitle = "Delicate floral perfume...",
                    imageRes = SharedR.drawable.ic_sample_tea_2,
                    rating = 4.5f,
                    savedCount = 70,
                    profileImageRes = SharedR.drawable.ic_profile_2,
                    authorName = "Mike",
                    likeCount = 24,
                    isLiked = true
                ),
                modifier = Modifier.width(170.dp)
            )
        }
    }
}