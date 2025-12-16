package com.leafy.features.analyze.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.leafy.features.analyze.data.TeaTypeRecord

@Composable
fun DonutChart(
    records: List<TeaTypeRecord>,
    modifier: Modifier = Modifier,
    chartSize: Dp = 150.dp,
    strokeWidth: Dp = 30.dp
) {
    Box(
        modifier = modifier.size(chartSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(chartSize)) {
            val width = size.width
            val height = size.height
            val canvasSize = Size(width, height)
            val strokeWidthPx = strokeWidth.toPx()

            var startAngle = -90f


            records.forEach { record ->

                val sweepAngle = (record.percentage.toFloat() / 100f) * 360f


                drawArc(
                    color = record.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    size = canvasSize,
                    style = Stroke(width = strokeWidthPx)
                )

                startAngle += sweepAngle
            }
        }


        Text(
            text = "${records.sumOf { it.percentage }}%",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}