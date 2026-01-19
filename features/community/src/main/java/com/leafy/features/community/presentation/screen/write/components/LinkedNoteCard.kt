package com.leafy.features.community.presentation.screen.write.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun LinkedNoteCard(
    title: String,
    teaType: String,
    date: String,
    thumbnailUri: Uri?,
    rating: Int?,
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            .border(1.dp, MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = thumbnailUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Linked Note",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(2.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )

            Spacer(Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$teaType  |  $date",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )

                if (rating != null) {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "$rating.0",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(start = 2.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        IconButton(onClick = onClear) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "연동 해제",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LinkedNoteCardPreview() {
    LeafyTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("노트 연동 카드 (이미지 + 날짜)", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 8.dp))

            LinkedNoteCard(
                title = "오설록 세작",
                teaType = "녹차",
                date = "2024.01.14",
                thumbnailUri = null,
                rating = 5,
                onClear = {}
            )
        }
    }
}