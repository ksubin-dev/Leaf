package com.leafy.features.community.presentation.screen.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
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
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.leafy.shared.ui.model.CommunityPostUiModel
import com.leafy.shared.common.clickableSingle
import com.leafy.shared.ui.component.LeafyProfileImage
import com.leafy.shared.ui.component.RatingStars
import com.leafy.shared.R as SharedR

@Composable
fun CommunityLargeCard(
    modifier: Modifier = Modifier,
    post: CommunityPostUiModel,
    onClick: () -> Unit,
    onProfileClick: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val context = LocalContext.current

    Card(
        modifier = modifier
            .width(220.dp)
            .height(265.dp)
            .clickableSingle(onClick = onClick),
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
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(post.imageUrls.firstOrNull())
                        .crossfade(true)
                        .build(),
                    contentDescription = post.title,
                    placeholder = painterResource(id = SharedR.drawable.ic_sample_tea_1),
                    error = painterResource(id = SharedR.drawable.ic_sample_tea_1),
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = post.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().height(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (post is CommunityPostUiModel.BrewingNote) {
                        if (post.rating > 0) {
                            RatingStars(
                                rating = post.rating,
                                size = 14.dp
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "Views",
                                tint = colors.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = post.viewCount,
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    LeafyProfileImage(
                        imageUrl = post.authorProfileUrl,
                        size = 28.dp,
                        onClick = { onProfileClick(post.authorId) }
                    )
                }
            }
        }
    }
}
