package com.leafy.features.note.ui.sections.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.leafy.features.note.ui.common.DetailSectionCard
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun PhotoDetailSection(
    imageUrls: List<String>,
    modifier: Modifier = Modifier,
    onPhotoClick: (String) -> Unit = {}
) {
    if (imageUrls.isEmpty()) return

    DetailSectionCard(
        title = "사진 (Photos)",
        modifier = modifier
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val rows = imageUrls.chunked(2)

            rows.forEach { rowImages ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PhotoItem(
                        url = rowImages[0],
                        modifier = Modifier.weight(1f),
                        onClick = { onPhotoClick(rowImages[0]) }
                    )

                    if (rowImages.size > 1) {
                        PhotoItem(
                            url = rowImages[1],
                            modifier = Modifier.weight(1f),
                            onClick = { onPhotoClick(rowImages[1]) }
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoItem(
    url: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1.3f)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = url,
            contentDescription = "Tea Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (url.isBlank()) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhotoDetailSectionPreview() {
    val mockImages = listOf(
        "https://example.com/1.jpg",
        "https://example.com/2.jpg",
        "https://example.com/3.jpg",
    )

    LeafyTheme {
        PhotoDetailSection(imageUrls = mockImages)
    }
}