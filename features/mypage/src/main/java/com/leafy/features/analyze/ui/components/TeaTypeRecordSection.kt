package com.leafy.features.analyze.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.analyze.data.TeaTypeRecord
import androidx.compose.ui.graphics.Color

@Composable
fun TeaTypeRecordSection(
    records: List<TeaTypeRecord>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 섹션 제목
        Text(
            text = "차 종류 기록",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DonutChart(
                records = records,
                chartSize = 150.dp,
                strokeWidth = 30.dp,
                modifier = Modifier.weight(1f)
            )

            // 2. 범례 (Legend)
            ChartLegend(
                records = records,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )
        }
    }
}

// -----------------------------------------------------------
// Preview를 위한 가짜(Dummy) 데이터 및 컴포넌트 스케치
// -----------------------------------------------------------

// ChartLegend 컴포넌트 스케치 (임시)
@Composable
private fun ChartLegend(records: List<TeaTypeRecord>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        records.forEach { record ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 색상 마커
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(record.color, MaterialTheme.shapes.extraSmall)
                )
                Spacer(Modifier.width(8.dp))
                // 차 이름과 비율
                Text(
                    text = "${record.teaName} (${record.percentage}%)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TeaTypeRecordSectionPreview() {
    val dummyTeaRecords = listOf(
        TeaTypeRecord("녹차", 28, Color(0xFF8DA385)),
        TeaTypeRecord("홍차", 35, Color(0xFFA35757)),
        TeaTypeRecord("우롱차", 18, Color(0xFFB5935A)),
        TeaTypeRecord("백차", 12, Color(0xFFD6C8B5)),
        TeaTypeRecord("기타", 7, Color(0xFFC7D6B5))
    )

    TeaTypeRecordSection(records = dummyTeaRecords)
}