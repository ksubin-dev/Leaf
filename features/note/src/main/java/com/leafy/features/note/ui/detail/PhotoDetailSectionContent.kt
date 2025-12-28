package com.leafy.features.note.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.leafy.features.note.ui.NoteUiState
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.WeatherType

@Composable
fun PhotoDetailSectionContent(
    uiState: NoteUiState,
    modifier: Modifier = Modifier
) {
    val photos = listOf(
        uiState.dryLeafUri,
        uiState.liquorUri,
        uiState.teawareUri,
        uiState.additionalUri
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(2) { rowIndex ->
                Row(
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(2) { colIndex ->
                        val photoUri = photos[rowIndex * 2 + colIndex]

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!photoUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = photoUri,
                                    contentDescription = "차 사진 ${rowIndex * 2 + colIndex + 1}",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Photo Detail Section Preview")
@Composable
fun PhotoDetailSectionPreview() {
    val mockUiState = NoteUiState(
        dryLeafUri = "https://images.unsplash.com/photo-1594631252845-29fc4586d52c",
        liquorUri = "https://images.unsplash.com/photo-1576092768241-dec231879fc3",
        teawareUri = null, // 비어있는 상태 테스트
        additionalUri = null, // 비어있는 상태 테스트
        teaName = "우전 녹차",
        weather = WeatherType.CLEAR
    )

    LeafyTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            PhotoDetailSectionContent(
                uiState = mockUiState
            )
        }
    }
}