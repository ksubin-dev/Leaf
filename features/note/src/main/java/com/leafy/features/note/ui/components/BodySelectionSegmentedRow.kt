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

@Composable
fun BodySelectionSegmentedRow(
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val options = listOf("Light", "Medium", "Full")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clip(RoundedCornerShape(999.dp)) // 둥근 전체 배경
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(999.dp)) // border 수정
            .background(colors.background),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEachIndexed { index, text ->
            val isSelected = selectedIndex == index

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (isSelected) colors.primaryContainer else Color.Transparent
                    )
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    color = if (isSelected) colors.primary
                    else colors.onPrimaryContainer
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
            BodySelectionSegmentedRow(selectedIndex = 1, onSelect = {})
        }
    }
}