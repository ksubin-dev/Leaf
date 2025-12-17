package com.leafy.features.mypage.presentation.calendar.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.leafy.features.mypage.presentation.calendar.ui.component.CalendarView
import com.leafy.features.mypage.presentation.calendar.data.RecentNote
import com.leafy.features.mypage.presentation.calendar.data.TeaSession
import com.leafy.features.mypage.presentation.calendar.data.CalendarDataSource
import com.leafy.features.mypage.ui.session.NoteCard
import com.leafy.features.mypage.ui.session.SessionCard
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    initialDate: LocalDate = LocalDate.of(2025, 12, 6),
    daysWithSessions: Set<Int> = CalendarDataSource.DefaultDaysWithSessions,
    sessions: List<TeaSession> = CalendarDataSource.DefaultTeaSessions,
    notes: List<RecentNote> = CalendarDataSource.DefaultRecentNotes,
    onDateSelected: (LocalDate) -> Unit = {},
    onViewSessionClicked: (String) -> Unit = {}
) {
    val colors = MaterialTheme.colorScheme

    // 현재 월 및 선택된 날짜 상태 관리
    var currentMonth by remember { mutableStateOf(LocalDate.of(initialDate.year, initialDate.month, 1)) }
    var selectedDate by remember { mutableStateOf(initialDate) }

    val onSelect: (LocalDate) -> Unit = { date ->
        selectedDate = date
        onDateSelected(date)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 60.dp)
    ) {
        item {
            CalendarView(
                currentMonth = currentMonth,
                selectedDate = selectedDate,
                daysWithSessions = daysWithSessions,
                onDateSelected = onSelect,
                onMonthChanged = { month -> currentMonth = month },
            )
        }

        item {
            Text(
                text = "Today's Sessions",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                color = colors.onBackground
            )
        }

        if (sessions.isNotEmpty()) {
            items(sessions, key = { it.id }) { session ->
                SessionCard(
                    session = session,
                    onViewClick = { onViewSessionClicked(session.id) }
                )
                Spacer(Modifier.height(8.dp))
            }
        } else {
            item {
                Text(
                    "선택하신 날짜에 세션이 없습니다.",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            }
        }


        item {
            Text(
                text = "Recent Notes",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                color = colors.onBackground
            )
        }


        items(notes, key = { it.id }) { note ->
            NoteCard(note = note)
            Spacer(Modifier.height(8.dp))
        }
    }
}