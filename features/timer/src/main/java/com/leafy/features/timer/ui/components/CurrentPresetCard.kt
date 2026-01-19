package com.leafy.features.timer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.common.clickableSingle
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.TeaType

@Composable
fun CurrentPresetCard(
    modifier: Modifier = Modifier,
    presetName: String?,
    targetTemp: Int,
    targetTime: Int,
    teaType: TeaType,
    onClickSettings: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickableSingle { onClickSettings() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "현재 레시피",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = presetName ?: "나만의 차",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                val timeMin = targetTime / 60
                val timeSec = targetTime % 60
                val timeString = if(timeSec > 0) "${timeMin}분 ${timeSec}초" else "${timeMin}분"

                Text(
                    text = "$targetTemp°C · $timeString · ${teaType.label}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = singleClick { onClickSettings() },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Change Preset",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "레시피 카드 모음")
@Composable
fun CurrentPresetCardPreview() {
    LeafyTheme {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CurrentPresetCard(
                    presetName = "아침에 마시는 녹차",
                    targetTemp = 75,
                    targetTime = 180,
                    teaType = TeaType.GREEN,
                    onClickSettings = {}
                )

                CurrentPresetCard(
                    presetName = null,
                    targetTemp = 95,
                    targetTime = 210,
                    teaType = TeaType.BLACK,
                    onClickSettings = {}
                )

                CurrentPresetCard(
                    presetName = "향긋한 우롱",
                    targetTemp = 90,
                    targetTime = 120,
                    teaType = TeaType.OOLONG,
                    onClickSettings = {}
                )
            }
    }
}