package com.leafy.features.mypage.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.leafy.shared.ui.util.CalendarUtil

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

    val firstDayOffset = CalendarUtil.getFirstDayOffset(currentMonth)
    val daysInMonth = CalendarUtil.getDaysInMonth(currentMonth)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = colors.background),
        border = androidx.compose.foundation.BorderStroke(1.dp, colors.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // 헤더 (이전/다음 버튼 포함)
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
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null)
                    }
                    IconButton(onClick = onNextMonth) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 요일 라벨
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

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.height(240.dp),
                userScrollEnabled = false,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(daysInMonth + firstDayOffset) { index ->
                    if (index >= firstDayOffset) {
                        val day = index - firstDayOffset + 1
                        CalendarDayItem(
                            day = day,
                            isSelected = day == selectedDay,
                            isToday = (day == java.time.LocalDate.now().dayOfMonth &&
                                    currentMonth == java.time.YearMonth.now()),
                            hasRecord = recordedDays.contains(day),
                            onClick = { onDateClick(day) }
                        )
                    } else {
                        Spacer(modifier = Modifier.size(40.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                onDateClick = {},    // 클릭 이벤트 빈 값 추가
                onPrevMonth = {},    // 이전달 클릭 빈 값 추가
                onNextMonth = {}     // 다음달 클릭 빈 값 추가
            )
        }
    }
}