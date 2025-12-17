package com.leafy.features.note.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.BodyType

@Composable
fun BodySelectionSegmentedRow(
    selectedBody: BodyType,
    onSelect: (BodyType) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    val options = BodyType.entries

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(999.dp))
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(999.dp))
            .background(colors.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { bodyType ->
            // 현재 그리는 항목이 선택된 항목인지 비교
            val isSelected = selectedBody == bodyType

            val label = bodyType.name.lowercase().replaceFirstChar { it.uppercase() }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (isSelected) colors.primaryContainer else Color.Transparent
                    )
                    .clickable { onSelect(bodyType) }, // Enum 자체를 전달
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    color = if (isSelected) colors.primary
                    else colors.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BodySelectionSegmentedRowPreview() {
    LeafyTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            BodySelectionSegmentedRow(
                selectedBody = BodyType.MEDIUM,
                onSelect = {}
            )
        }
    }
}