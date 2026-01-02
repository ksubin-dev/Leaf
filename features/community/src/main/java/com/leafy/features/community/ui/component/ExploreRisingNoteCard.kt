package com.leafy.features.community.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import coil.compose.AsyncImage
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.ui.component.RatingStars
import com.leafy.shared.R as SharedR

@Composable
fun ExploreRisingNoteCard(
    note: ExploreNoteUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    showHotBadge: Boolean = true,
    hotLabel: String = "급상승"
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .width(220.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = note.imageUrl,
                    contentDescription = note.title,
                    placeholder = painterResource(SharedR.drawable.ic_sample_tea_1),
                    error = painterResource(SharedR.drawable.ic_sample_tea_1),
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )

                if (showHotBadge) {
                    Surface(
                        modifier = Modifier.padding(10.dp),
                        shape = RoundedCornerShape(999.dp),
                        color = colors.error
                    ) {
                        Text(
                            text = hotLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.onPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.onSurface,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                RatingStars(
                    rating = note.rating.toInt(),
                    size = 16.dp
                )

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
                        AsyncImage(
                            model = note.authorProfileUrl,
                            contentDescription = null,
                            placeholder = painterResource(SharedR.drawable.ic_profile_3),
                            error = painterResource(SharedR.drawable.ic_profile_3),
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = note.authorName ?: "Unknown",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(
                                id = if (note.isLiked) SharedR.drawable.ic_like_filled else SharedR.drawable.ic_like
                            ),
                            contentDescription = null,
                            tint = if (note.isLiked) colors.error else colors.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp).clickable { /* TODO */ }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = note.likeCount.toString(),
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
                note = ExploreNoteUi(
                    id = "1",
                    title = "Chamomile Dreams",
                    subtitle = "Soothing, honey-like sweetness...",
                    imageUrl = null,
                    rating = 4.0f,
                    authorName = "Sarah",
                    likeCount = 12,
                    isLiked = false
                ),
                modifier = Modifier.width(170.dp)
            )
        }
    }
}