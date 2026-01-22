package com.leafy.features.mypage.presentation.main.session

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leafy.features.mypage.presentation.main.component.BrewingCalendar
import com.leafy.features.mypage.presentation.main.component.BrewingRecordCard
import com.leafy.features.mypage.presentation.main.component.BrewingRecordEmptyCard
import com.leafy.features.mypage.presentation.main.component.StatsCard
import com.leafy.features.mypage.presentation.main.MyPageUiState
import com.leafy.shared.common.singleClick
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

@Composable
fun MyPageCalendarSection(
    uiState: MyPageUiState,
    onDateClick: (LocalDate) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onDetailClick: (String) -> Unit,
    onViewAllClick: (LocalDate) -> Unit
) {
    val currentYearMonth = remember(uiState.currentYear, uiState.currentMonth) {
        YearMonth.of(uiState.currentYear, uiState.currentMonth)
    }

    val recordedDays = remember(uiState.calendarNotes) {
        uiState.calendarNotes.map { note ->
            Instant.ofEpochMilli(note.date)
                .atZone(ZoneId.systemDefault())
                .dayOfMonth
        }.distinct()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        BrewingCalendar(
            currentMonth = currentYearMonth,
            selectedDay = uiState.selectedDate.dayOfMonth,
            recordedDays = recordedDays,
            onDateClick = { day ->
                onDateClick(currentYearMonth.atDay(day))
            },
            onPrevMonth = onPrevMonth,
            onNextMonth = onNextMonth
        ) {
            Column(modifier = Modifier.padding(top = 10.dp)) {

                val representativeNote = uiState.selectedDateNotes.firstOrNull()

                if (representativeNote != null) {
                    BrewingRecordCard(
                        imageUrl = representativeNote.metadata.imageUrls.firstOrNull(),
                        teaName = representativeNote.teaInfo.name,
                        metaInfo = "${representativeNote.teaInfo.type.name} · ${representativeNote.recipe.waterTemp}°C",
                        rating = representativeNote.rating.stars,
                        onEditClick = { onEditClick(representativeNote.id) },
                        onCardClick = { onDetailClick(representativeNote.id) }
                    )

                    if (uiState.selectedDateNotes.size > 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = singleClick { onViewAllClick(uiState.selectedDate) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = "오늘의 모든 기록 보기 (${uiState.selectedDateNotes.size}) →",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    BrewingRecordEmptyCard(onAddClick = onAddClick)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val analysis = uiState.userAnalysis

                    StatsCard(
                        modifier = Modifier.weight(1f),
                        label = "총 우림",
                        value = "${analysis?.totalBrewingCount ?: 0}",
                        unit = "회"
                    )
                    StatsCard(
                        modifier = Modifier.weight(1f),
                        label = "연속 스트릭",
                        value = "${analysis?.currentStreakDays ?: 0}",
                        unit = "일"
                    )
                    StatsCard(
                        modifier = Modifier.weight(1f),
                        label = "이번 달",
                        value = "${analysis?.monthlyBrewingCount ?: 0}",
                        unit = "회"
                    )
                }
            }
        }
    }
}