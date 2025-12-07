package com.leafy.features.note.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.common.CustomSlider
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun FinishSliderRow(
    level: Float,
    onLevelChange: (Float) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {

        // 제목 + 상태 텍스트 (Balanced 등)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LeafyFieldLabel(text = "Finish (후미)")
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = when {
                    level < 0.3f -> "Clean (깔끔)"
                    level > 0.7f -> "Astringent (떫음)"
                    else -> "Balanced (균형 잡힘)"
                },
                style = MaterialTheme.typography.bodySmall,
                color = colors.primary,
                fontWeight = FontWeight.SemiBold
            )
        }

        // 0.0f..1.0f 범위 슬라이더
        CustomSlider(
            value = level,
            onValueChange = onLevelChange,
            maxValue = 1f,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Clean", style = MaterialTheme.typography.bodySmall, color = colors.onBackground)
            Text("Astringent", style = MaterialTheme.typography.bodySmall, color = colors.onBackground)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FinishSliderRowPreview() {
    LeafyTheme {
        FinishSliderRow(
            level = 0.5f,
            onLevelChange = {}
        )
    }
}