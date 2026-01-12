package com.leafy.features.note.ui.detail.sections

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
import coil.compose.AsyncImage
import com.leafy.features.note.ui.common.DetailSectionCard
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun PhotoDetailSection(
    imageUrls: List<String>,
    modifier: Modifier = Modifier,
    onPhotoClick: (String) -> Unit = {} // 사진 확대 보기 등을 위한 클릭 콜백
) {
    // 이미지가 없으면 섹션을 아예 숨기거나, 안내 메시지를 띄웁니다.
    // 여기서는 섹션 자체를 안 그리는 대신, 빈 상태라도 카드는 보여주되 안내를 넣는 방식으로 합니다.
    if (imageUrls.isEmpty()) return

    DetailSectionCard(
        title = "사진 (Photos)",
        modifier = modifier
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // 리스트를 2개씩 묶어서 처리 (Grid 효과)
            val rows = imageUrls.chunked(2)

            rows.forEach { rowImages ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 첫 번째 이미지
                    PhotoItem(
                        url = rowImages[0],
                        modifier = Modifier.weight(1f),
                        onClick = { onPhotoClick(rowImages[0]) }
                    )

                    // 두 번째 이미지 (있으면 표시, 없으면 빈 공간 채움)
                    if (rowImages.size > 1) {
                        PhotoItem(
                            url = rowImages[1],
                            modifier = Modifier.weight(1f),
                            onClick = { onPhotoClick(rowImages[1]) }
                        )
                    } else {
                        // 짝이 안 맞을 때 빈 공간 유지
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
            .aspectRatio(1.3f) // 4:3 비율 정도 (이미지와 비슷하게)
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

        // 로딩 실패 혹은 URL이 비었을 때 아이콘 표시 (선택 사항)
        if (url.isBlank()) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

// ----------------------------------------------------------------------
// Preview
// ----------------------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun PhotoDetailSectionPreview() {
    val mockImages = listOf(
        "https://example.com/1.jpg",
        "https://example.com/2.jpg",
        "https://example.com/3.jpg",
        // 3장일 때 테스트 (마지막 칸이 비어 있어야 함)
    )

    LeafyTheme {
        PhotoDetailSection(imageUrls = mockImages)
    }
}