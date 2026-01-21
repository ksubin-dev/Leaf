package com.leafy.features.mypage.presentation.analysis.component

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

@Composable
fun TeaDistributionChart(
    distribution: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    val surfaceColor = MaterialTheme.colorScheme.surface.toArgb()
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()

    val colors = listOf(
        Color(0xFF81C784).toArgb(),
        Color(0xFF4CAF50).toArgb(),
        Color(0xFF388E3C).toArgb(),
        Color(0xFFA5D6A7).toArgb(),
        Color(0xFF1B5E20).toArgb()
    )

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp),
        factory = { context ->
            PieChart(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                description.isEnabled = false
                legend.isEnabled = true
                legend.textColor = onSurfaceColor

                isDrawHoleEnabled = true
                setHoleColor(surfaceColor)
                transparentCircleRadius = 0f
                holeRadius = 50f

                centerText = "Tea\nType"
                setCenterTextSize(14f)
                setCenterTextColor(onSurfaceColor)

                animateY(1000)
            }
        },
        update = { chart ->
            if (distribution.isEmpty()) return@AndroidView

            val entries = distribution.map { (key, value) ->
                PieEntry(value.toFloat(), key)
            }

            val dataSet = PieDataSet(entries, "").apply {
                this.colors = colors
                sliceSpace = 3f
                valueTextColor = Color.White.toArgb()
                valueTextSize = 12f
            }

            chart.data = PieData(dataSet)
            chart.invalidate()
        }
    )
}