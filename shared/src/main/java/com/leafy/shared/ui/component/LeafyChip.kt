package com.leafy.shared.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LeafyChip(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedContainerColor: Color = MaterialTheme.colorScheme.secondary,
    selectedContentColor: Color = MaterialTheme.colorScheme.onSecondary
) {
    val colors = MaterialTheme.colorScheme

    val backgroundColor = if (isSelected) selectedContainerColor else Color.Transparent
    val contentColor = if (isSelected) selectedContentColor else colors.onSurfaceVariant
    val border = if (isSelected) null else BorderStroke(1.dp, colors.outline.copy(alpha = 0.5f))

    Surface(
        modifier = modifier.clickable { onClick() },
        shape = CircleShape,
        color = backgroundColor,
        contentColor = contentColor,
        border = border,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}