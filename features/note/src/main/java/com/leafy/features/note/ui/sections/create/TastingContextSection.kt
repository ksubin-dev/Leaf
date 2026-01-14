package com.leafy.features.note.ui.sections.create

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.note.ui.common.NoteInputTextField
import com.leafy.features.note.ui.common.NoteSectionHeader
import com.leafy.shared.R
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.ui.utils.LeafyTimeUtils
import com.subin.leafy.domain.model.WeatherType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TastingContextSection(
    modifier: Modifier = Modifier,
    dateTime: String,
    withPeople: String,
    selectedWeather: WeatherType?,
    onDateTimeChange: (String) -> Unit,
    onWeatherSelected: (WeatherType) -> Unit,
    onWithPeopleChange: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(modifier = modifier.fillMaxWidth()) {
        NoteSectionHeader(
            icon = painterResource(id = R.drawable.ic_note_section_context),
            title = "테이스팅 환경"
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            NoteInputTextField(
                value = dateTime,
                onValueChange = {},
                label = "날짜",
                placeholder = "YYYY-MM-DD",
                readOnly = true,
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_calendar),
                        contentDescription = "달력 열기",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(top = 24.dp)
                    .clickable { showDatePicker = true }
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val dateString = LeafyTimeUtils.millisToDateString(millis)
                                onDateTimeChange(dateString)
                            }
                            showDatePicker = false
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

        Text(
            text = "날씨",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            WeatherType.entries.forEach { type ->
                WeatherOptionButton(
                    type = type,
                    isSelected = selectedWeather == type,
                    onClick = { onWeatherSelected(type) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        NoteInputTextField(
            value = withPeople,
            onValueChange = onWithPeopleChange,
            label = "함께한 사람 (선택)",
            placeholder = "친구, 가족, 혼자...",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun WeatherOptionButton(
    type: WeatherType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    val backgroundColor = if (isSelected) colors.primaryContainer else colors.surface
    val borderColor = if (isSelected) colors.primary else colors.outlineVariant
    val textColor = if (isSelected) colors.primary else colors.onSurfaceVariant

    val (iconRes, label) = when (type) {
        WeatherType.SUNNY -> R.drawable.ic_weather_clear to "맑음"
        WeatherType.CLOUDY -> R.drawable.ic_weather_cloudy to "흐림"
        WeatherType.RAINY -> R.drawable.ic_weather_rainy to "비"
        WeatherType.SNOWY -> R.drawable.ic_weather_snowy to "눈"
        WeatherType.INDOOR -> R.drawable.ic_weather_indoor to "실내"
    }

    Column(
        modifier = modifier
            .height(70.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = textColor
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TastingContextSectionPreview() {
    LeafyTheme {
        var date by remember { mutableStateOf(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)) }
        var weather by remember { mutableStateOf<WeatherType?>(WeatherType.SUNNY) }
        var people by remember { mutableStateOf("") }

        Box(modifier = Modifier.padding(16.dp)) {
            TastingContextSection(
                modifier = Modifier.fillMaxWidth(),
                withPeople = people,
                dateTime = date,
                selectedWeather = weather,
                onDateTimeChange = {date = it },
                onWeatherSelected = { weather = it },
                onWithPeopleChange = { people = it }
            )
        }
    }
}