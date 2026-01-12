package com.leafy.features.note.ui.sections

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.leafy.features.note.ui.common.NoteSectionHeader
import com.leafy.shared.R
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun PhotosSection(
    selectedImages: List<Uri>,
    onAddImages: (List<Uri>) -> Unit,
    onRemoveImage: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            onAddImages(uris)
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // 1. 섹션 헤더 (아이콘 + 제목 + 개수 표시)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            NoteSectionHeader(
                icon = painterResource(id = R.drawable.ic_note_section_photos),
                title = "사진"
            )
            // 개수 표시 (0/5) - 헤더 옆에 작게 표시
            Text(
                text = "(${selectedImages.size}/5)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.padding(bottom = 12.dp))

        // 2. 가로 스크롤 이미지 리스트
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            // [A] 추가 버튼 (항상 맨 앞)
            item {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            // 이미지 전용 피커 실행
                            launcher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "사진 추가",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // [B] 선택된 이미지들
            items(selectedImages) { uri ->
                Box(modifier = Modifier.size(80.dp)) {
                    // 이미지
                    AsyncImage(
                        model = uri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                    )

                    // 삭제 버튼 (우상단 X)
                    IconButton(
                        onClick = { onRemoveImage(uri) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .offset(x = 6.dp, y = (-6).dp)
                            .background(Color.Black.copy(alpha = 0.6f), shape = RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "삭제",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------
// Preview
// ----------------------------------------------------------------------
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PhotosSectionPreview() {
    LeafyTheme {
        // [더미 데이터 생성]
        // R.drawable.ic_leaf 같은 리소스 ID를 Uri 문자열로 변환합니다.
        // 주의: 실제 앱 패키지명에 맞춰야 렌더링될 수 있습니다.
        // 여기서는 예시로 빈 리스트와 가짜 Uri를 섞어서 보여드립니다.

        // 프리뷰에서는 실제 이미지를 로드하기 까다로우므로,
        // 1. 빈 상태 프리뷰
        // 2. 이미지가 있는 상태 프리뷰 (Uri.parse 사용)

        Column(modifier = Modifier.padding(16.dp)) {
            Text("1. 빈 상태", style = MaterialTheme.typography.labelMedium)
            PhotosSection(
                selectedImages = emptyList(),
                onAddImages = {},
                onRemoveImage = {}
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("2. 이미지가 있는 상태 (가짜 데이터)", style = MaterialTheme.typography.labelMedium)
            // 안드로이드 리소스 URI 형식: android.resource://[패키지명]/[리소스ID]
            // 프리뷰 렌더링 시에는 Coil이 이 URI를 해석하지 못해 깨진 이미지나 placeholder가 나올 수 있습니다.
            // UI 배치(레이아웃) 확인 용도로만 사용하세요.
            val dummyUri = Uri.parse("android.resource://com.leafy.app/drawable/${R.drawable.ic_leaf}")

            PhotosSection(
                selectedImages = listOf(dummyUri, dummyUri, dummyUri),
                onAddImages = {},
                onRemoveImage = {}
            )
        }
    }
}