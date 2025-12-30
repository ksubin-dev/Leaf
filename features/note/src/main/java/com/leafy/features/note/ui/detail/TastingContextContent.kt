package com.leafy.features.note.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.common.DetailInfoRow
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.NoteContext
import com.subin.leafy.domain.model.WeatherType
import com.leafy.shared.R as SharedR

@Composable
fun TastingContextContent(
    context: NoteContext,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        DetailInfoRow(label = "Date & Time", value = context.dateTime)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Weather",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                val weatherIconRes = when (context.weather) {
                    WeatherType.CLEAR -> SharedR.drawable.ic_weather_clear
                    WeatherType.CLOUDY -> SharedR.drawable.ic_weather_cloudy
                    WeatherType.RAINY -> SharedR.drawable.ic_weather_rainy
                    WeatherType.SNOWY -> SharedR.drawable.ic_weather_snowy
                    else -> SharedR.drawable.ic_weather_indoor
                }

                Icon(
                    imageVector = ImageVector.vectorResource(id = weatherIconRes),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = context.weather?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "Indoor",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "With whom",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = context.withPeople.ifBlank { "Solo" },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TastingContextDetailSectionPreview() {
    LeafyTheme {
        TastingContextContent(
            context = NoteContext(
                dateTime = "Nov 13, 2024 • 3:30 PM",
                weather = WeatherType.CLOUDY,
                withPeople = ""
            )
        )
    }
}