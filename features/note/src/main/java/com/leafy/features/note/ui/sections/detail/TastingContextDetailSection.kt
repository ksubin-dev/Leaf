package com.leafy.features.note.ui.sections.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.common.DetailInfoRow
import com.leafy.features.note.ui.common.DetailSectionCard
import com.leafy.shared.R
import com.leafy.shared.ui.component.LeafyChip
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.utils.LeafyTimeUtils
import com.subin.leafy.domain.model.NoteMetadata
import com.subin.leafy.domain.model.WeatherType

@Composable
fun TastingContextSection(
    createdTimestamp: Long,
    metadata: NoteMetadata,
    modifier: Modifier = Modifier
) {
    DetailSectionCard(
        title = "테이스팅 환경 (Tasting Context)",
        modifier = modifier
    ) {
        val dateString = LeafyTimeUtils.formatLongToString(createdTimestamp)
        DetailInfoRow(label = "날짜", value = dateString)

        if (metadata.weather != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "날씨",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val (iconRes, label) = when (metadata.weather) {
                        WeatherType.SUNNY -> R.drawable.ic_weather_clear to "맑음"
                        WeatherType.CLOUDY -> R.drawable.ic_weather_cloudy to "흐림"
                        WeatherType.RAINY -> R.drawable.ic_weather_rainy to "비"
                        WeatherType.SNOWY -> R.drawable.ic_weather_snowy to "눈"
                        WeatherType.INDOOR -> R.drawable.ic_weather_indoor to "실내"
                        else -> R.drawable.ic_weather_clear to "알 수 없음"
                    }

                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        val withWhom = metadata.mood.ifBlank { "혼자" }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "함께한 사람",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LeafyChip(
                text = withWhom,
                isSelected = true,
                onClick = {},
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TastingContextSectionPreview() {
    LeafyTheme {
        val mockMetadata = NoteMetadata(
            weather = WeatherType.CLOUDY,
            mood = "친구들",
            imageUrls = emptyList()
        )
        TastingContextSection(
            createdTimestamp = System.currentTimeMillis(),
            metadata = mockMetadata
        )
    }
}