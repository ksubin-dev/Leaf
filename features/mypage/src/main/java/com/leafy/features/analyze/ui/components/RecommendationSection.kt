package com.leafy.features.analyze.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leafy.features.analyze.data.TeaRecommendation

@Composable
fun RecommendationSection(
    modifier: Modifier = Modifier,
    recommendations: List<TeaRecommendation>
) {
    Column(modifier = modifier) {
        Text(
            text = "취향 기반 추천",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            recommendations.forEach { recommendation ->
                RecommendationCard(
                    recommendation = recommendation,
                )
            }
        }
    }
}