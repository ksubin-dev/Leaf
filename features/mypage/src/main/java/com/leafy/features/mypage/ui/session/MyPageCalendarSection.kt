package com.leafy.features.mypage.ui.session

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.leafy.features.mypage.ui.MyPageUiState
import com.leafy.features.mypage.ui.component.BrewingCalendar
import com.leafy.features.mypage.ui.component.BrewingRecordCard
import com.leafy.features.mypage.ui.component.BrewingRecordEmptyCard

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyPageCalendarSection(
    uiState: MyPageUiState,
    onDateClick: (Int) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        BrewingCalendar(
            currentMonth = uiState.currentMonth,
            selectedDay = uiState.selectedDay,
            recordedDays = uiState.recordedDays,
            onDateClick = onDateClick,
            onPrevMonth = onPrevMonth,
            onNextMonth = onNextMonth
        ) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                if (uiState.selectedRecord != null) {
                    BrewingRecordCard(
                        teaName = uiState.selectedRecord.teaName,
                        metaInfo = uiState.selectedRecord.metaInfo,
                        rating = uiState.selectedRecord.rating,
                        onEditClick = onEditClick
                    )
                } else {
                    BrewingRecordEmptyCard(onAddClick = onAddClick)
                }
            }
        }
    }
}