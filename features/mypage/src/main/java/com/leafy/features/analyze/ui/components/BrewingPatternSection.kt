package com.leafy.features.analyze.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leafy.features.analyze.data.BrewingPatternData
import com.leafy.shared.R as SharedR

@Composable
fun BrewingPatternSection(
    modifier: Modifier = Modifier,
    data: BrewingPatternData
) {
    Column(modifier = modifier) {
        Text(
            text = "우려내기 패턴",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            PatternMetricItem(
                item = MetricItem(
                    label = "평균 물 온도",
                    value = data.avgTemp,
                    iconResId = SharedR.drawable.ic_temp
                ),
                modifier = Modifier.weight(1f)
            )

            PatternMetricItem(
                item = MetricItem(
                    label = "평균 우림 시간",
                    value = data.avgTime,
                    iconResId = SharedR.drawable.ic_timer
                ),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

        Spacer(Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                PatternMetricItem(
                    item = MetricItem(
                        label = "가장 많이 사용한",
                        value = "티백",
                        iconResId = SharedR.drawable.ic_tool
                    ),
                    modifier = Modifier.weight(1f)
                )

                PatternMetricItem(
                    item = MetricItem(
                        label = "평균 우림 횟수",
                        value = data.avgCount,
                        iconResId = SharedR.drawable.ic_refresh
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

            Spacer(Modifier.height(16.dp))

            PatternMetricItem(
                item = MetricItem(
                    label = "가장 많이 마시는 시간",
                    value = data.peakTime,
                    iconResId = SharedR.drawable.ic_clock
                ),

                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}