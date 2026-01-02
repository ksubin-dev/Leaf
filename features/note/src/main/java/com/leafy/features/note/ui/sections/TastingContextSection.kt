package com.leafy.features.note.ui.sections

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.subin.leafy.domain.model.WeatherType
import com.leafy.features.note.ui.components.LeafyFieldLabel
import com.leafy.features.note.ui.components.WeatherOptionButton
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.ui.utils.LeafyTimeUtils
import java.time.Instant
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TastingContextSection(
    modifier: Modifier = Modifier,
    dateTime: String,
    selectedWeather: WeatherType,
    withPeople: String,
    onDateTimeChange: (String) -> Unit,
    onWeatherSelected: (WeatherType) -> Unit,
    onWithPeopleChange: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    val initialDate = remember(dateTime) { LeafyTimeUtils.parseToDate(dateTime) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
    )
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = SharedR.drawable.ic_note_section_context),
                contentDescription = "Tasting Context",
                tint = colors.primary,
                modifier = Modifier.height(18.dp).padding(end = 6.dp)
            )
            Text(
                text = "Tasting Context",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LeafyFieldLabel(text = "Date")
        OutlinedTextField(
            value = LeafyTimeUtils.formatToDisplay(dateTime),
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clickable { showDatePicker = true },
            readOnly = true,
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = colors.onSurface,
                disabledBorderColor = colors.outline,
                disabledTrailingIconColor = colors.primary,
                disabledPlaceholderColor = colors.tertiary
            ),
            trailingIcon = {
                Icon(
                    painter = painterResource(id = SharedR.drawable.ic_calendar),
                    contentDescription = "Pick date"
                )
            }
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                            datePickerState.selectedDateMillis?.let { millis ->
                                val selectedDate = Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.of("UTC"))
                                    .toLocalDate()
                                onDateTimeChange(LeafyTimeUtils.formatToString(selectedDate))
                            }
                        }
                    ) { Text("확인") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("취소") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LeafyFieldLabel(text = "Weather")
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WeatherType.entries.forEach { weatherType ->
                WeatherOptionButton(
                    type = weatherType,
                    label = weatherType.name.lowercase().replaceFirstChar { it.uppercase() },
                    iconRes = when(weatherType) {
                        WeatherType.CLEAR -> SharedR.drawable.ic_weather_clear
                        WeatherType.CLOUDY -> SharedR.drawable.ic_weather_cloudy
                        WeatherType.RAINY -> SharedR.drawable.ic_weather_rainy
                        WeatherType.SNOWY -> SharedR.drawable.ic_weather_snowy
                        WeatherType.INDOOR -> SharedR.drawable.ic_weather_indoor
                    },
                    selectedWeather = selectedWeather,
                    onSelected = onWeatherSelected,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LeafyFieldLabel(text = "With")
        OutlinedTextField(
            value = withPeople,
            onValueChange = onWithPeopleChange,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            singleLine = true,
            placeholder = { Text(text = "누구와 함께 마셨나요?", color = colors.tertiary) }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun TastingContextSectionPreview() {
    LeafyTheme {
        TastingContextSection(
            dateTime = "2025-12-30",
            selectedWeather = WeatherType.CLEAR,
            withPeople = "수빈",
            onDateTimeChange = {},
            onWeatherSelected = {},
            onWithPeopleChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}