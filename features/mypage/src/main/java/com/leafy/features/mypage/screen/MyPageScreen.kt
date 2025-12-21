package com.leafy.features.mypage.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.mypage.ui.MyPageUiState
import com.leafy.features.mypage.ui.MyPageViewModel
import com.leafy.features.mypage.ui.component.ProfileHeader
import com.leafy.features.mypage.ui.component.QuickTimerAction
import com.leafy.features.mypage.ui.session.MyPageCalendarSection
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.*
import com.subin.leafy.domain.model.id.NoteId
import com.subin.leafy.domain.model.id.UserId

@Composable
fun MyPageScreen(
    viewModel: MyPageViewModel,
    onSettingsClick: () -> Unit,
    onAddRecordClick: () -> Unit,
    onEditRecordClick: (String) -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyPageContent(
        uiState = uiState,
        onSettingsClick = onSettingsClick,
        onDateClick = viewModel::onDateSelected,
        onPrevMonth = { viewModel.changeMonth(-1) },
        onNextMonth = { viewModel.changeMonth(1) },
        onAddRecordClick = onAddRecordClick,
        onEditRecordClick = onEditRecordClick
    )
}

@Composable
private fun MyPageContent(
    uiState: MyPageUiState,
    onSettingsClick: () -> Unit,
    onDateClick: (Int) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddRecordClick: () -> Unit,
    onEditRecordClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {

            if (uiState.user != null && uiState.userStats != null) {
                ProfileHeader(
                    user = uiState.user,
                    stats = uiState.userStats,
                    onSettingsClick = onSettingsClick
                )
            } else if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            QuickTimerAction(
                presetName = "데일리 녹차",
                onClick = {}
            )

            MyPageCalendarSection(
                uiState = uiState,
                onDateClick = onDateClick,
                onPrevMonth = onPrevMonth,
                onNextMonth = onNextMonth,
                onAddClick = onAddRecordClick,
                onEditClick = {
                    uiState.selectedRecord?.let { onEditRecordClick(it.id.value) }
                }
            )

            // 하단 여백을 위한 스페이서
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
private fun MyPageScreenPreview() {
    // 프리뷰용 Mock 데이터 구성
    val mockUser = User(
        id = UserId("1"),
        username = "Felix",
        profileImageUrl = null
    )

    val mockStats = UserStats(
        weeklyBrewingCount = 5,
        averageRating = 4.8,
        preferredTea = "Oolong",
        averageBrewingTime = "3:20",
        monthlyBrewingCount = 18
    )

    val mockRecord = BrewingRecord(
        id = NoteId("note_1"),
        teaName = "Alishan Oolong",
        metaInfo = "2025.12.21 · 3rd Steep",
        rating = 5,
        date = java.time.LocalDate.now()
    )

    LeafyTheme {
        MyPageContent(
            uiState = MyPageUiState(
                user = mockUser,
                userStats = mockStats,
                recordedDays = listOf(1, 5, 12, 15, 21),
                selectedRecord = mockRecord,
                isLoading = false
            ),
            onSettingsClick = {},
            onDateClick = {},
            onPrevMonth = {},
            onNextMonth = {},
            onAddRecordClick = {},
            onEditRecordClick = {}
        )
    }
}

