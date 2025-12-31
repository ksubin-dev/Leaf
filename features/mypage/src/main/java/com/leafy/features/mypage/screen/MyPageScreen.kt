package com.leafy.features.mypage.screen


import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.mypage.ui.MyPageUiEffect
import com.leafy.features.mypage.ui.MyPageUiState
import com.leafy.features.mypage.ui.MyPageViewModel
import com.leafy.features.mypage.ui.component.ProfileHeader
import com.leafy.features.mypage.ui.session.MyPageCalendarSection
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyPageScreen(
    viewModel: MyPageViewModel,
    onSettingsClick: () -> Unit,
    onAddRecordClick: (String) -> Unit,
    onEditRecordClick: (String) -> Unit,
    onRecordDetailClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MyPageUiEffect.NavigateToDetail -> {
                    onRecordDetailClick(effect.noteId)
                }
            }
        }
    }

    MyPageContent(
        uiState = uiState,
        onSettingsClick = onSettingsClick,
        onDateClick = viewModel::onDateSelected,
        onPrevMonth = { viewModel.changeMonth(-1) },
        onNextMonth = { viewModel.changeMonth(1) },
        onAddRecordClick = { onAddRecordClick(uiState.selectedDateString) },
        onEditRecordClick = onEditRecordClick,
        onRecordDetailClick = { noteId ->
            viewModel.onRecordDetailClick(noteId)
        }
    )
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MyPageContent(
    uiState: MyPageUiState,
    onSettingsClick: () -> Unit,
    onDateClick: (Int) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onAddRecordClick: () -> Unit,
    onEditRecordClick: (String) -> Unit,
    onRecordDetailClick: (String) -> Unit
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
            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            uiState.user?.let { user ->
                ProfileHeader(
                    user = user,
                    stats = uiState.userStats ?: UserStats(0, 0.0, "-", "-", 0),
                    onSettingsClick = onSettingsClick
                )
            }

            MyPageCalendarSection(
                uiState = uiState,
                onDateClick = onDateClick,
                onPrevMonth = onPrevMonth,
                onNextMonth = onNextMonth,
                onAddClick = onAddRecordClick,
                onEditClick = onEditRecordClick,
                onDetailClick = onRecordDetailClick
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
private fun MyPageScreenPreview() {
    val mockUser = User(
        id = "user_1",
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
        id = "note_1",
        teaName = "Alishan Oolong",
        metaInfo = "3회 우림",
        rating = 5,
        dateString = "2025-12-21"
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
            onEditRecordClick = {},
            onRecordDetailClick = {},
        )
    }
}
