package com.leafy.features.community.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.leafy.shared.ui.component.RatingStars
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.R as SharedR

@Composable
fun ExploreFollowingNoteCard(
    note: ExploreNoteUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLikeClick: () -> Unit = {},
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
                    model = note.authorProfileUrl,
                    contentDescription = note.authorName,
                    placeholder = painterResource(SharedR.drawable.ic_profile_1),
                    error = painterResource(SharedR.drawable.ic_profile_1),
                    modifier = Modifier.size(32.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = note.authorName ?: "Unknown",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.onSurface
                    )
                    Text(
                        text = note.timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.primary
                    )
                }

                IconButton(onClick = { /* TODO */ }) {
                    Icon(
                        painter = painterResource(id = SharedR.drawable.ic_more_vert),
                        contentDescription = "More",
                        tint = colors.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = note.imageUrl,
                    contentDescription = note.title,
                    placeholder = painterResource(SharedR.drawable.ic_sample_tea_1),
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                    shape = RoundedCornerShape(999.dp),
                    color = colors.surface.copy(alpha = 0.85f)
                ) {
                    Text(
                        text = note.metaInfo.split("·").firstOrNull()?.trim() ?: "Tea",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = colors.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = colors.primary
            )
            Text(
                text = note.metaInfo,
                style = MaterialTheme.typography.labelSmall,
                color = colors.secondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = note.description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                note.brewingChips.forEach { chip ->
                    ExploreFollowingChip(text = chip)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


            Row(verticalAlignment = Alignment.CenterVertically) {
                RatingStars(rating = note.rating.toInt(), size = 14.dp)

                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = String.format("%.1f", note.rating),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (note.reviewLabel.isNotEmpty()) {
                    ExploreFollowingChip(text = note.reviewLabel)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (note.comment.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceVariant.copy(alpha = 0.5f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = note.comment,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        painter = painterResource(id = if (note.isLiked) SharedR.drawable.ic_like_filled else SharedR.drawable.ic_like),
                        contentDescription = "Like",
                        modifier = Modifier.size(20.dp),
                        tint = if (note.isLiked) colors.error else colors.secondary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))

                IconButton(onClick = { /* 댓글 기능 TODO */ }) {
                    Icon(
                        painter = painterResource(id = SharedR.drawable.ic_comment),
                        contentDescription = "Comment",
                        modifier = Modifier.size(20.dp),
                        tint = colors.primary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))

                IconButton(onClick = onBookmarkClick) {
                    Icon(
                        painter = painterResource(id = SharedR.drawable.ic_bookmark),
                        contentDescription = "Bookmark",
                        modifier = Modifier.size(20.dp),
                        tint = if (note.isSaved) colors.primary else colors.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    note.likerProfileUrls.take(3).forEachIndexed { index, url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .offset(x = (index * 12).dp)
                                .zIndex((10 - index).toFloat())
                                .clip(CircleShape)
                                .border(1.5.dp, colors.surface, CircleShape),
                            contentScale = ContentScale.Crop,
                            error = painterResource(SharedR.drawable.ic_profile_2)
                        )
                    }
                }

                val likeTextOffset = if (note.likerProfileUrls.isNotEmpty()) (note.likerProfileUrls.take(3).size * 12 + 15).dp else 0.dp

                Text(
                    text = "${note.likeCount}명이 좋아합니다",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.secondary,
                    modifier = Modifier.padding(start = likeTextOffset)
                )
            }
        }
    }
}

@Composable
private fun ExploreFollowingChip(text: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExploreFollowingNoteCardPreview() {
    LeafyTheme {
        // 샘플 데이터 생성
        val sampleNote = ExploreNoteUi(
            id = "1",
            title = "동정오룡차 (Dong Ding Oolong)",
            subtitle = "대만 · 중배화 · 반구형",
            authorName = "민지",
            authorProfileUrl = null,
            timeAgo = "2시간 전",
            imageUrl = null,
            description = "은은한 꽃향과 부드러운 과일향이 조화롭게 어우러진 오룽차, 목넘김이 매끄럽고 여운이 깁니다.",
            rating = 4.5f,
            brewingChips = listOf("95℃", "3m", "5g", "1st Infusion"),
            reviewLabel = "Rebrew 가능",
            comment = "오늘 아침에 마신 차 중 최고였어요. 3회까지 우려봤는데 2번째 우림이 가장 좋았답니다. 😊",
            likeCount = 23,
            isLiked = true,
            likerProfileUrls = listOf("", "", "")
        )

        Box(modifier = Modifier.padding(16.dp)) {
            ExploreFollowingNoteCard(
                note = sampleNote,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}