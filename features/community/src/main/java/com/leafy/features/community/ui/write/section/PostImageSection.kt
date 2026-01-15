package com.leafy.features.community.ui.write.section

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.leafy.shared.R
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun PostImageSection(
    selectedUris: List<Uri>,
    onAddImage: () -> Unit,
    onRemoveImage: (Uri) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        if (selectedUris.isNotEmpty()) {
            AsyncImage(
                model = selectedUris.first(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (selectedUris.size > 1) {
                    Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), shape = CircleShape) {
                        Text(
                            "+${selectedUris.size - 1}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
                IconButton(
                    onClick = onAddImage,
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape).size(36.dp)
                ) {
                    Icon(painterResource(R.drawable.ic_note_photo_add), null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                }
            }

            IconButton(
                onClick = { onRemoveImage(selectedUris.first()) },
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), CircleShape).size(32.dp)
            ) {
                Icon(Icons.Rounded.Close, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        } else {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(painterResource(R.drawable.ic_note_photo_add), null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(8.dp))
                Text("사진을 추가해주세요", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Button(onClick = onAddImage, modifier = Modifier.padding(top = 16.dp)) {
                    Text("앨범 열기")
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PostImageSectionPreview() {
    LeafyTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text("1. 이미지가 없을 때", style = MaterialTheme.typography.titleSmall)
            PostImageSection(
                selectedUris = emptyList(),
                onAddImage = {},
                onRemoveImage = {}
            )

            Text("2. 이미지가 선택되었을 때 (다중)", style = MaterialTheme.typography.titleSmall)
            PostImageSection(
                selectedUris = listOf(Uri.EMPTY, Uri.EMPTY, Uri.EMPTY),
                onAddImage = {},
                onRemoveImage = {}
            )
        }
    }
}