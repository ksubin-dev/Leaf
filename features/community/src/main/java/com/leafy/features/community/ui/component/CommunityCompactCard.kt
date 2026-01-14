package com.leafy.features.community.ui.component

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.leafy.features.community.ui.model.CommunityPostUiModel
import com.leafy.shared.R as SharedR

@Composable
fun CommunityCompactCard(
    modifier: Modifier = Modifier,
    post: CommunityPostUiModel,
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
            AsyncImage(
                model = post.imageUrls.firstOrNull(),
                contentDescription = null,
                placeholder = painterResource(id = SharedR.drawable.ic_sample_tea_1),
                error = painterResource(id = SharedR.drawable.ic_sample_tea_1),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.surfaceVariant)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val subtitle = post.teaType ?: post.authorName
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
                        .clickable { onBookmarkClick() }
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