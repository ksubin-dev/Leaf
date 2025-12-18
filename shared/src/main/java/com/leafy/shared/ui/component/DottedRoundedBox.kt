package com.leafy.shared.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun DottedRoundedBox(
    modifier: Modifier = Modifier,
    cornerRadiusDp: Float = 12f,
    borderWidthDp: Float = 1f,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    dashLength: Float = 10f,
    gapLength: Float = 6f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(cornerRadiusDp.dp))
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                color = borderColor,
                style = Stroke(
                    width = borderWidthDp.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(dashLength, gapLength),
                        0f
                    )
                ),
                cornerRadius = CornerRadius(cornerRadiusDp.dp.toPx())
            )
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}