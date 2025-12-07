package com.leafy.features.note.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.common.NotePhotoSlot
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun PhotosSection(
    onClickDryLeaf: () -> Unit,
    onClickTeaLiquor: () -> Unit,
    onClickTeaware: () -> Unit,
    onClickAdditional: () -> Unit,
    modifier: Modifier = Modifier
) {

    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = SharedR.drawable.ic_note_section_photos),
                contentDescription = "Photos",
                tint = colors.primary,
                modifier = Modifier
                    .height(18.dp)
                    .padding(end = 6.dp)
            )
            Text(
                text = "Photos",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = colors.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NotePhotoSlot(
                    title = "Dry Leaf",
                    iconRes = SharedR.drawable.ic_leaf,
                    onClick = onClickDryLeaf,
                    modifier = Modifier.weight(1f)
                )
                NotePhotoSlot(
                    title = "Tea Liquor",
                    iconRes = SharedR.drawable.ic_note_photo_tea_liquor,
                    onClick = onClickTeaLiquor,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                NotePhotoSlot(
                    title = "Teaware",
                    iconRes = SharedR.drawable.ic_note_photo_teaware,
                    onClick = onClickTeaware,
                    modifier = Modifier.weight(1f)
                )
                NotePhotoSlot(
                    title = "Additional",
                    iconRes = SharedR.drawable.ic_note_photo_add,
                    onClick = onClickAdditional,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotosSectionPreview() {
    LeafyTheme {
        PhotosSection(
            onClickDryLeaf = {},
            onClickTeaLiquor = {},
            onClickTeaware = {},
            onClickAdditional = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}