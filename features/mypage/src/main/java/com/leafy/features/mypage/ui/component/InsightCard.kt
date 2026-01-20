package com.leafy.features.mypage.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.subin.leafy.domain.model.BrewingInsight
import com.subin.leafy.domain.model.InsightAction

@Composable
fun InsightCard(
    insight: BrewingInsight,
    modifier: Modifier = Modifier,
    onClick: (InsightAction?) -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = insight.action != null) { onClick(insight.action) },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = insight.emoji, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = buildAnnotatedString {
                        append(insight.description)
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.inverseSurface,
                        lineHeight = 22.sp
                    )
                )
            }
        }
    }
}