package com.leafy.features.note.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subin.leafy.domain.model.WeatherType
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme

@Composable
fun WeatherOptionButton(
    modifier: Modifier = Modifier,
    type: WeatherType,
    label: String,
    iconRes: Int,
    selectedWeather: WeatherType,
    onSelected: (WeatherType) -> Unit
) {

    val colors = MaterialTheme.colorScheme

    val isSelected = selectedWeather == type

    val borderColor = if (isSelected) colors.outline.copy(alpha = 0.8f) else colors.outlineVariant
    val backgroundColor = if (isSelected) colors.background.copy(alpha = 0.5f) else colors.background
    val textColor = if (isSelected) colors.onBackground else colors.onBackground

    Column(
        modifier = modifier
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .background(backgroundColor)
            .clickable { onSelected(type) }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = Color.Unspecified,
            modifier = Modifier.height(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherOptionButtonPreview() {
    LeafyTheme {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WeatherOptionButton(
                type = WeatherType.CLEAR,
                label = "Clear",
                iconRes = SharedR.drawable.ic_weather_clear,
                selectedWeather = WeatherType.CLEAR,
                onSelected = {},
                modifier = Modifier.weight(1f)
            )
            WeatherOptionButton(
                type = WeatherType.RAINY,
                label = "Rainy",
                iconRes = SharedR.drawable.ic_weather_rainy,
                selectedWeather = WeatherType.CLEAR,
                onSelected = {},
                modifier = Modifier.weight(1f)
            )
        }
    }
}