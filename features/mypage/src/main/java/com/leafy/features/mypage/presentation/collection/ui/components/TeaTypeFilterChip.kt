package com.leafy.features.mypage.presentation.collection.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TeaTypeFilterChip(
    type: String,
    isSelected: Boolean,
    onChipClicked: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    val containerColor = if (isSelected) colors.primary else colors.surfaceContainerHigh
    val contentColor = if (isSelected) colors.onPrimary else colors.onSurfaceVariant

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        color = containerColor,
        contentColor = contentColor,
        modifier = Modifier
            .clickable { onChipClicked(type) }
    ) {
        Text(
            text = type,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}