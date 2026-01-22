package com.leafy.features.mypage.presentation.collection.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuantityIndicator(quantity: String) {

    val colors = MaterialTheme.colorScheme

    val (dotColor, displayText) = when (quantity.uppercase()) {
        "PLENTY" -> colors.primary to quantity.replaceFirstChar { it.uppercase() }
        "LOW" -> colors.secondary to quantity.replaceFirstChar { it.uppercase() }
        "EMPTY" -> colors.error to quantity.replaceFirstChar { it.uppercase() }
        else -> colors.onSurfaceVariant to quantity.replaceFirstChar { it.uppercase() }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(8.dp)) {
            drawCircle(color = dotColor)
        }

        Spacer(Modifier.width(8.dp))

        Text(
            text = displayText,
            style = MaterialTheme.typography.bodyMedium,
            color = dotColor
        )
    }
}