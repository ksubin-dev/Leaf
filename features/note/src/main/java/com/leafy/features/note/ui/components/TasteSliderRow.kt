package com.leafy.features.note.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.common.CustomSlider
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun TasteSliderRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {

    val colors = MaterialTheme.colorScheme
    val textColor = colors.onBackground

    Row(
        modifier = modifier.fillMaxWidth().height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 왼쪽 텍스트 (Sweet / Sour …)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = textColor),
            modifier = Modifier.width(72.dp)
        )
        CustomSlider(
            value = value,
            onValueChange = onValueChange,
            maxValue = 5f,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        )

        // 오른쪽 숫자
        Text(
            text = value.toInt().toString(),
            style = MaterialTheme.typography.bodyMedium.copy(color = textColor),
            modifier = Modifier.padding(start = 8.dp).width(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TasteSliderRowPreview() {
    LeafyTheme {
        TasteSliderRow(
            label = "Sweet",
            value = 4f,
            onValueChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}