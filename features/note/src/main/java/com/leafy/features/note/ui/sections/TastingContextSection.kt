package com.leafy.features.note.ui.sections

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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // 섹션 타이틀
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

        // Date & Time
        LeafyFieldLabel(text = "Date & Time")
        OutlinedTextField(
            value = dateTime,
            onValueChange = { /* readOnly */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clickable { showDatePicker = true },
            readOnly = true,
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        painter = painterResource(id = SharedR.drawable.ic_calendar),
                        contentDescription = "Pick date",
                        tint = colors.primary
                    )
                }
            }
        )

        // DatePicker Dialog 로직
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                            datePickerState.selectedDateMillis?.let { millis ->
                                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val formattedDate = formatter.format(Date(millis))
                                onDateTimeChange(formattedDate)
                            }
                        }
                    ) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) { DatePicker(state = datePickerState) }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weather
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

        // With People
        LeafyFieldLabel(text = "With")
        OutlinedTextField(
            value = withPeople,
            onValueChange = onWithPeopleChange,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            singleLine = true,
            placeholder = { Text(text = "e.g. Minjae, Subin", color = colors.tertiary) }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TastingContextSectionPreview() {
    LeafyTheme {
        TastingContextSection(
            dateTime = "12/17/2025",
            selectedWeather = WeatherType.CLEAR,
            withPeople = "Subin",
            onDateTimeChange = {},
            onWeatherSelected = {},
            onWithPeopleChange = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}