package com.leafy.features.mypage.presentation.calendar.ui.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.leafy.shared.R as SharedR
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(
    modifier: Modifier = Modifier,
    currentMonth: LocalDate,
    selectedDate: LocalDate,
    daysWithSessions: Set<Int>,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChanged: (LocalDate) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val firstDayOfMonth = currentMonth.withDayOfMonth(1)

    Column(modifier = modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChanged(currentMonth.minusMonths(1)) }) {
                Icon(painterResource(SharedR.drawable.ic_arrow_back), contentDescription = "Previous Month")
            }
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = colors.onBackground
            )
            IconButton(onClick = { onMonthChanged(currentMonth.plusMonths(1)) }) {
                Icon(painterResource(SharedR.drawable.ic_arrow_forward), contentDescription = "Next Month")
            }
        }


        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            val daysOfWeek = remember { DayOfWeek.values().sortedBy { it.value % 7 } }

            daysOfWeek.forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.US),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val daysInMonth = currentMonth.lengthOfMonth()
        val startDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7

        val totalDays = 42
        val daysList = remember(currentMonth) {
            (0 until totalDays).map { index ->
                val dayOfMonth = index - startDayOfWeek + 1
                if (dayOfMonth >= 1 && dayOfMonth <= daysInMonth) {
                    currentMonth.withDayOfMonth(dayOfMonth) to true
                } else {
                    null to false
                }
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            daysList.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    week.forEach { (date, isCurrentMonth) ->
                        if (date != null) {
                            val isSelected = date == selectedDate
                            val hasSession = daysWithSessions.contains(date.dayOfMonth)
                            CalendarDayItem(
                                date = date,
                                isSelected = isSelected,
                                hasSession = hasSession,
                                isCurrentMonth = isCurrentMonth,
                                onClick = { onDateSelected(date) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                        }
                    }
                }
            }
        }
    }
}