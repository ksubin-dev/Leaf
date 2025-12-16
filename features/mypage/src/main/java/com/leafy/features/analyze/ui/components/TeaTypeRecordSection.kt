package com.leafy.features.analyze.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.leafy.features.analyze.data.TeaTypeRecord
import com.leafy.shared.ui.theme.LeafyTheme

// -----------------------------------------------------------
// UI 전용 데이터 모델 (중간 매핑 모델)
// -----------------------------------------------------------

data class TeaRecordUiModel(
    val teaName: String,
    val percentage: Int,
    val color: Color
)

private fun mapTeaTypeToUiModel(
    record: TeaTypeRecord,
    index: Int,
    colors: ColorScheme
): TeaRecordUiModel {
    val assignedColor = when (index) {
        0 -> colors.primary
        1 -> colors.error
        2 -> colors.secondary
        3 -> colors.errorContainer
        4 -> colors.primaryContainer
        5 -> colors.secondaryContainer
        else -> Color.Gray
    }

    return TeaRecordUiModel(
        teaName = record.teaName,
        percentage = record.percentage,
        color = assignedColor
    )
}

@Composable
fun TeaTypeRecordSection(
    modifier: Modifier = Modifier,
    records: List<TeaTypeRecord>
) {
    val colors = MaterialTheme.colorScheme
    val uiRecords: List<TeaRecordUiModel> = remember(records, colors) {
        records.mapIndexed { index, record ->
            mapTeaTypeToUiModel(record, index, colors)
        }
    }

    Column(modifier = modifier) {
        Text(
            text = "차 종류 기록",
            style = MaterialTheme.typography.headlineSmall,
            color = colors.onBackground
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val donutChartRecords = uiRecords.map {
                DonutChartRecord(
                    percentage = it.percentage,
                    color = it.color
                )
            }

            DonutChart(
                records = donutChartRecords,
                chartSize = 150.dp,
                strokeWidth = 30.dp,
                modifier = Modifier.weight(1f)
            )
            ChartLegend(
                records = uiRecords,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )
        }
    }
}

// -----------------------------------------------------------
// ChartLegend 컴포넌트 (TeaRecordUiModel 사용)
// -----------------------------------------------------------

@Composable
private fun ChartLegend(records: List<TeaRecordUiModel>, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Column(modifier = modifier) {
        records.forEach { record ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(record.color, MaterialTheme.shapes.extraSmall)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${record.teaName} (${record.percentage}%)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurface
                )
            }
        }
    }
}

// -----------------------------------------------------------
// Preview (순수한 TeaTypeRecord 사용)
// -----------------------------------------------------------

@Preview(showBackground = true)
@Composable
private fun TeaTypeRecordSectionPreview() {
    val pureDummyRecords = listOf(
        TeaTypeRecord("녹차", 28),
        TeaTypeRecord("홍차", 35),
        TeaTypeRecord("우롱차", 18),
        TeaTypeRecord("백차", 12),
        TeaTypeRecord("말차", 5),
        TeaTypeRecord("황차", 2)
    )

    LeafyTheme {
        TeaTypeRecordSection(records = pureDummyRecords)
    }
}