package com.leafy.features.analyze.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leafy.shared.R as SharedR
import com.leafy.features.analyze.data.TopTeaRanking

@Composable
fun RankingItemCard(
    ranking: TopTeaRanking,
    modifier: Modifier = Modifier
) {

    val colors = MaterialTheme.colorScheme
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (ranking.rank) {
                1 -> colors.primaryContainer
                2 -> colors.secondaryContainer
                3 -> colors.tertiaryContainer
                else -> colors.surface
            }
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${ranking.rank}",
                style = MaterialTheme.typography.headlineLarge,
                color = colors.onBackground,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.width(40.dp)
            )

            Spacer(Modifier.width(16.dp))

            Image(
                painter = painterResource(id = SharedR.drawable.ic_sample_tea_10),
                contentDescription = ranking.name,
                modifier = Modifier.size(50.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ranking.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.onSurface,
                    maxLines = 1
                )

                // 평점
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating Star",
                        tint = colors.error,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", ranking.rating),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${ranking.count}회",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "마심",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant
                )
            }
        }
    }
}