package com.leafy.features.mypage.ui.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leafy.features.mypage.ui.MyPageUiState
import com.leafy.features.mypage.ui.component.InsightCard
import com.subin.leafy.domain.model.BrewingInsight

@Composable
fun MyPageInsightSection(
    uiState: MyPageUiState,
    onInsightClick: (BrewingInsight) -> Unit,
    onViewFullReportClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = "차 마시는 습관 인사이트",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // 1. 인사이트 카드 리스트
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            uiState.brewingInsights.take(2).forEach { insight ->
                InsightCard(
                    insight = insight,
                    onClick = { onInsightClick(insight) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. 상세 리포트 보기 버튼 (HTML 디자인 참고)
        TextButton(
            onClick = onViewFullReportClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "상세 리포트 보기 →",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}