package com.leafy.features.note.ui.sections.create

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.common.NoteSectionHeader
import com.leafy.shared.R
import com.leafy.shared.ui.component.LeafyImagePicker
import com.leafy.shared.ui.theme.LeafyTheme
import androidx.core.net.toUri

@Composable
fun PhotosSection(
    selectedImages: List<Uri>,
    onAddImages: (List<Uri>) -> Unit,
    onRemoveImage: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            NoteSectionHeader(
                icon = painterResource(id = R.drawable.ic_note_section_photos),
                title = "사진"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${selectedImages.size} / 5",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        LeafyImagePicker(
            selectedImages = selectedImages,
            onImagesSelected = onAddImages,
            onImageRemoved = onRemoveImage,
            maxItems = 5
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun PhotosSectionPreview() {
    LeafyTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("1. 빈 상태", style = MaterialTheme.typography.labelMedium)
            PhotosSection(
                selectedImages = emptyList(),
                onAddImages = {},
                onRemoveImage = {}
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("2. 이미지가 있는 상태 (가짜 데이터)", style = MaterialTheme.typography.labelMedium)
            val dummyUri = "android.resource://com.leafy.app/drawable/${R.drawable.ic_leaf}".toUri()

            PhotosSection(
                selectedImages = listOf(dummyUri, dummyUri, dummyUri),
                onAddImages = {},
                onRemoveImage = {}
            )
        }
    }
}