package com.leafy.features.note.ui.sections

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.data.WeatherType // Import
import com.leafy.features.note.ui.components.LeafyFieldLabel // Import
import com.leafy.features.note.ui.components.WeatherOptionButton // Import
import com.leafy.shared.R as SharedR
import com.leafy.shared.ui.theme.LeafyTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TastingContextSection(
    modifier: Modifier = Modifier
) {

    val colors = MaterialTheme.colorScheme

    var dateTime by remember { mutableStateOf("") }
    var selectedWeather by remember { mutableStateOf(WeatherType.CLEAR) }
    var withPeople by remember { mutableStateOf("") }


    // DatePicker 상태
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    Column(modifier = modifier) {

        // 섹션 타이틀
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = SharedR.drawable.ic_note_section_context),
                contentDescription = "Tasting Context",
                tint = colors.primary,
                modifier = Modifier
                    .height(18.dp)
                    .padding(end = 6.dp)
            )
            Text(
                text = "Tasting Context",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
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

        // DatePicker Dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDatePicker = false
                            val millis = datePickerState.selectedDateMillis
                            if (millis != null) {
                                val date = Date(millis)
                                val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                                dateTime = formatter.format(date)
                            }
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weather
        LeafyFieldLabel(text = "Weather")
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 분리된 WeatherOptionButton 컴포넌트 사용
            WeatherOptionButton(
                type = WeatherType.CLEAR,
                label = "Clear",
                iconRes = SharedR.drawable.ic_weather_clear,
                selectedWeather = selectedWeather,
                onSelected = { selectedWeather = it },
                modifier = Modifier.weight(1f)
            )
            WeatherOptionButton(
                type = WeatherType.CLOUDY,
                label = "Cloudy",
                iconRes = SharedR.drawable.ic_weather_cloudy,
                selectedWeather = selectedWeather,
                onSelected = { selectedWeather = it },
                modifier = Modifier.weight(1f)
            )
            WeatherOptionButton(
                type = WeatherType.RAINY,
                label = "Rainy",
                iconRes = SharedR.drawable.ic_weather_rainy,
                selectedWeather = selectedWeather,
                onSelected = { selectedWeather = it },
                modifier = Modifier.weight(1f)
            )
            WeatherOptionButton(
                type = WeatherType.SNOWY,
                label = "Snowy",
                iconRes = SharedR.drawable.ic_weather_snowy,
                selectedWeather = selectedWeather,
                onSelected = { selectedWeather = it },
                modifier = Modifier.weight(1f)
            )
            WeatherOptionButton(
                type = WeatherType.INDOOR,
                label = "Indoor",
                iconRes = SharedR.drawable.ic_weather_indoor,
                selectedWeather = selectedWeather,
                onSelected = { selectedWeather = it },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // With People
        LeafyFieldLabel(text = "With")
        OutlinedTextField(
            value = withPeople,
            onValueChange = { withPeople = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            singleLine = true,
            placeholder = {
                Text(
                    text = "e.g. Minjae, Subin",
                    color = colors.tertiary
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TastingContextSectionPreview() {
    LeafyTheme {
        TastingContextSection(
            modifier = Modifier.padding(16.dp)
        )
    }
}