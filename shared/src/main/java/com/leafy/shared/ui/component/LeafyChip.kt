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
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    // 선택되었을 때: Primary 색상 채움 / 안 됐을 때: 테두리만
    val backgroundColor = if (isSelected) colors.primary else Color.Transparent
    val contentColor = if (isSelected) colors.onPrimary else colors.onSurfaceVariant
    val border = if (isSelected) null else BorderStroke(1.dp, colors.outline.copy(alpha = 0.5f))

    Surface(
        modifier = modifier.clickable { onClick() },
        shape = CircleShape, // 완전 둥근 알약 모양
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