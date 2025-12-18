package com.leafy.features.timer.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CircularTimerDisplay(
    modifier: Modifier = Modifier,
    progress: Float,
    formattedTime: String,
    currentInfusion: Int
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = "TimerProgressAnimation"
    )

    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.size(280.dp)) {
            drawCircle(
                color = trackColor,
                style = Stroke(width = 20f)
            )
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = 360 * animatedProgress,
                useCenter = false,
                style = Stroke(width = 25f, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "INFUSION $currentInfusion",
                style = MaterialTheme.typography.labelMedium,
                color = primaryColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = formattedTime,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}