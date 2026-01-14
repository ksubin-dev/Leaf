package com.leafy.features.community.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ExploreTagChip(
    tag: ExploreTagUi,
    modifier: Modifier = Modifier,
    onClick: (ExploreTagUi) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier
            .background(
                color = colors.primaryContainer,
                shape = RoundedCornerShape(999.dp)
            )
            .clickable { onClick(tag) }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "#${tag.label}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = colors.secondary
            )

            if (tag.isTrendingUp) {
                Text(
                    text = " ↑",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.secondary
                )
            }
        }
    }
}