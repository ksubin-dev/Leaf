package com.leafy.features.analyze.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leafy.features.analyze.data.TopTeaRanking

@Composable
fun TopTeaRankingSection(
    modifier: Modifier = Modifier,
    rankings: List<TopTeaRanking>
) {
    Column(modifier = modifier) {
        Text(
            text = "반복해서 마신 차 TOP 3",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            rankings.forEach { ranking ->
                RankingItemCard(ranking = ranking)
            }
        }
    }
}