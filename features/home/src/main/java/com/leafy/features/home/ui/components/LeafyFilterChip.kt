package com.leafy.features.home.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
    selected: Boolean
) {
    val colors = MaterialTheme.colorScheme

    val bg = if (selected) colors.primary else colors.surfaceVariant
    val fg = if (selected) colors.onPrimary else colors.onSurfaceVariant

    Surface(
        shape = RoundedCornerShape(50),
        color = bg
    ) {
        Text(
            text = text,
            color = fg,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LeafyFilterChipPreview() {
    LeafyTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            LeafyFilterChip(text = "이번 주", selected = true)
            Spacer(modifier = Modifier.height(8.dp))
            LeafyFilterChip(text = "녹차", selected = false)
        }
    }
}