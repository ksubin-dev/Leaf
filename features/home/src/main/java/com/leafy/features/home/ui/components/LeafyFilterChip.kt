package com.leafy.features.home.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun LeafyFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    val bg = if (selected) colors.primary else colors.surfaceVariant.copy(alpha = 0.5f)
    val fg = if (selected) colors.onPrimary else colors.onSurfaceVariant

    Surface(
        shape = RoundedCornerShape(50),
        color = bg,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = fg,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}