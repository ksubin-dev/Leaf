package com.leafy.features.community.presentation.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.leafy.shared.ui.model.CommunityPostUiModel
import com.leafy.shared.common.clickableSingle
import com.leafy.shared.R as SharedR

@Composable
fun CommunityCompactCard(
    modifier: Modifier = Modifier,
    post: CommunityPostUiModel,
    rank: Int? = null,
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickableSingle(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.TopStart) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(post.imageUrls.firstOrNull())
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    placeholder = painterResource(id = SharedR.drawable.ic_sample_tea_1),
                    error = painterResource(id = SharedR.drawable.ic_sample_tea_1),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.surfaceVariant)
                )

                if (rank != null) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(topStart = 12.dp, bottomEnd = 8.dp),
                        modifier = Modifier.size(width = 24.dp, height = 20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = rank.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val subtitle = when (post) {
                    is CommunityPostUiModel.BrewingNote -> post.teaType
                    is CommunityPostUiModel.General -> post.authorName
                }

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (post.isBookmarked) SharedR.drawable.ic_bookmark_filled
                        else SharedR.drawable.ic_bookmark_outline
                    ),
                    contentDescription = "Bookmark",
                    tint = if (post.isBookmarked) colors.primary else colors.onSurfaceVariant,
                    modifier = Modifier
                        .size(24.dp)
                        .clickableSingle { onBookmarkClick() }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = post.bookmarkCount,
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}