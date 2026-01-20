package com.leafy.features.mypage.ui.component


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.ui.utils.LeafyTimeUtils

@Composable
fun BrewingCalendar(
    modifier: Modifier = Modifier,
    currentMonth: java.time.YearMonth,
    selectedDay: Int,
    recordedDays: List<Int>,
    onDateClick: (Int) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    bottomContent: @Composable () -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    val firstDayOffset = LeafyTimeUtils.getFirstDayOffset(currentMonth)
    val daysInMonth = LeafyTimeUtils.getDaysInMonth(currentMonth)
    val today = LeafyTimeUtils.now()

    val isCurrentMonth = currentMonth.year == today.year && currentMonth.monthValue == today.monthValue

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.background),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${currentMonth.year}년 ${currentMonth.monthValue}월",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    IconButton(onClick = onPrevMonth) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "이전 달")
                    }
                    IconButton(onClick = onNextMonth) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "다음 달")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.tertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val totalSlots = daysInMonth + firstDayOffset
            val rows = (totalSlots + 6) / 7

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (row in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (col in 0 until 7) {
                            val index = row * 7 + col
                            val day = index - firstDayOffset + 1

                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                if (index in firstDayOffset until (daysInMonth + firstDayOffset)) {
                                    CalendarDayItem(
                                        day = day,
                                        isSelected = day == selectedDay,
                                        isToday = isCurrentMonth && day == today.dayOfMonth,
                                        hasRecord = recordedDays.contains(day),
                                        onClick = { onDateClick(day) }
                                    )
                                } else {
                                    Spacer(modifier = Modifier.size(40.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            bottomContent()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BrewingCalendarPreview() {
    LeafyTheme {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
            .padding(vertical = 20.dp)) {

            BrewingCalendar(
                currentMonth = java.time.YearMonth.of(2025, 12),
                selectedDay = 17,
                recordedDays = listOf(1, 3, 5, 8, 10, 15, 17),
                onDateClick = {},
                onPrevMonth = {},
                onNextMonth = {}
            )
        }
    }
}