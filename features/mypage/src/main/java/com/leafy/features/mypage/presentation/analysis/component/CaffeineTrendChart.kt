package com.leafy.features.mypage.presentation.analysis.component

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import androidx.core.graphics.toColorInt

@Composable
fun CaffeineTrendChart(
    weeklyTrend: List<Int>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val surfaceColor = MaterialTheme.colorScheme.surface.toArgb()
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp),
        factory = { context ->
            LineChart(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                legend.isEnabled = false
                isDragEnabled = false
                setScaleEnabled(false)
                setPinchZoom(false)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = onSurfaceVariant
                    textSize = 12f
                    valueFormatter = IndexAxisValueFormatter(listOf("월", "화", "수", "목", "금", "토", "일"))
                    granularity = 1f
                }

                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = "#F0F0F0".toColorInt()
                    axisMinimum = 0f
                    textColor = onSurfaceVariant
                }

                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            if (weeklyTrend.isEmpty()) return@AndroidView

            val entries = weeklyTrend.mapIndexed { index, value ->
                Entry(index.toFloat(), value.toFloat())
            }

            val dataSet = LineDataSet(entries, "카페인 섭취량").apply {
                mode = LineDataSet.Mode.CUBIC_BEZIER
                cubicIntensity = 0.2f

                color = primaryColor
                lineWidth = 3f

                setDrawCircles(true)
                setCircleColor(primaryColor)
                circleRadius = 4f
                setDrawCircleHole(true)
                circleHoleColor = surfaceColor

                setDrawValues(false)

                setDrawFilled(true)
                fillColor = primaryColor
                fillAlpha = 50
            }

            chart.data = LineData(dataSet)
            chart.invalidate()
            chart.animateY(1000)
        }
    )
}