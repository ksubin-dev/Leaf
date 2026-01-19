package com.leafy.features.timer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.ui.utils.LeafyTimeUtils

@Composable
fun InfusionRecordItem(
    modifier: Modifier = Modifier,
    count: Int,
    timeSeconds: Int,
    temp: Int,
    onDelete: () -> Unit
) {
    val timeStr = LeafyTimeUtils.formatSecondsToHangul(timeSeconds)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f),verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$count",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "우림 기록",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = "$temp°C · $timeStr",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        IconButton(onClick = singleClick{onDelete()}) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "삭제",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InfusionRecordItemPreview() {
    LeafyTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            InfusionRecordItem(
                count = 1,
                timeSeconds = 180,
                temp = 90,
                onDelete = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
            InfusionRecordItem(
                count = 2,
                timeSeconds = 45,
                temp = 95,
                onDelete = {}
            )
        }
    }
}