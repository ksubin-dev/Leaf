package com.leafy.features.community.ui.write.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.leafy.features.community.ui.model.NoteSelectionUiModel
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun NoteSelectionSheet(
    notes: List<NoteSelectionUiModel>,
    onNoteClick: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .heightIn(max = 500.dp)
    ) {
        Text(
            text = "시음 노트 선택",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        if (notes.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Text("작성된 노트가 없습니다.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notes) { note ->
                    NoteSelectionItem(note = note, onClick = { onNoteClick(note.id) })
                }
            }
        }
    }
}

@Composable
fun NoteSelectionItem(
    note: NoteSelectionUiModel,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = note.thumbnailUri,
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
                    text = note.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${note.teaType} | ${note.date}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )

                    if (note.rating > 0) {
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "${note.rating}.0",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun NoteSelectionSheetPreview() {
    val sampleNotes = listOf(
        NoteSelectionUiModel(
            id = "1",
            title = "에티오피아 예가체프 G1",
            teaType = "핸드드립",
            date = "2024.01.14",
            rating = 5,
            thumbnailUri = null
        ),
        NoteSelectionUiModel(
            id = "2",
            title = "콜롬비아 수프리모",
            teaType = "에스프레소",
            date = "2024.01.10",
            rating = 4,
            thumbnailUri = null
        ),
        NoteSelectionUiModel(
            id = "3",
            title = "제주 유기농 녹차",
            teaType = "침출차",
            date = "2023.12.25",
            rating = 3,
            thumbnailUri = null
        )
    )

    LeafyTheme {
        NoteSelectionSheet(
            notes = sampleNotes,
            onNoteClick = {},
            onDismissRequest = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun NoteSelectionSheetEmptyPreview() {
    LeafyTheme {
        NoteSelectionSheet(
            notes = emptyList(),
            onNoteClick = {},
            onDismissRequest = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NoteSelectionItemPreview() {
    LeafyTheme {
        NoteSelectionItem(
            note = NoteSelectionUiModel(
                id = "1",
                title = "파나마 게이샤",
                teaType = "핸드드립",
                date = "2024.01.15",
                rating = 5,
                thumbnailUri = null
            ),
            onClick = {}
        )
    }
}