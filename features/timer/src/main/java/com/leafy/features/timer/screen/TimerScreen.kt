package com.leafy.features.timer.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.timer.ui.TimerUiState
import com.leafy.features.timer.ui.TimerViewModel
import com.leafy.features.timer.ui.components.PresetBottomSheet
import com.leafy.features.timer.ui.sections.TimerMainSection
import com.leafy.features.timer.ui.sections.TimerSessionSection
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.InfusionRecord

@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    onNavigateToNote: (String) -> Unit,
    onNavigateToSettings: () -> Unit // 이제 바텀시트를 쓰므로 필요 없으면 제거 가능
) {
    val uiState by viewModel.uiState.collectAsState()

    // 1. 바텀시트 로직 추가
    if (uiState.isBottomSheetOpen) {
        PresetBottomSheet(
            presets = uiState.presets,
            selectedPresetId = uiState.selectedPreset.id,
            onPresetClick = { preset ->
                viewModel.selectPreset(preset)
            },
            onDismiss = viewModel::closePresetSheet
        )
    }

    TimerScreenContent(
        uiState = uiState,
        onToggle = viewModel::toggleTimer,
        onReset = viewModel::resetTimer,
        onRecord = viewModel::recordInfusion,
        onSaveToNote = {
            val recordsJson = viewModel.getRecordsAsJson()
            onNavigateToNote(recordsJson)
        },
        // 2. 톱니바퀴 클릭 시 네비게이션 대신 바텀시트 열기 함수 호출
        // 1. 지금처럼 바텀시트만 띄울 거라면 viewModel::openPresetSheet 를 넣습니다.
        // 2. 만약 "바텀시트도 띄우고 + 다른 일도 하고 싶다"면 아래처럼 람다로 묶습니다.
        onSettingsClick = {
            viewModel.openPresetSheet()
            // onNavigateToSettings() // 만약 다른 화면으로도 가야한다면 주석 해제
        }
    )
}

@Composable
private fun TimerScreenContent(
    uiState: TimerUiState,
    onToggle: () -> Unit,
    onReset: () -> Unit,
    onRecord: () -> Unit,
    onSaveToNote: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            TimerMainSection(
                uiState = uiState,
                onToggle = onToggle,
                onReset = onReset,
                onRecord = onRecord,
                onSettingsClick = onSettingsClick
            )

            Spacer(modifier = Modifier.height(32.dp))

            TimerSessionSection(
                records = uiState.records,
                onSaveToNote = onSaveToNote,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TimerScreenPreview() {
    val mockRecords = listOf(
        InfusionRecord(count = 1, timeSeconds = 60, formattedTime = "01:00"),
        InfusionRecord(count = 2, timeSeconds = 30, formattedTime = "00:30")
    )

    LeafyTheme {
        TimerScreenContent(
            uiState = TimerUiState(
                records = mockRecords,
                currentInfusion = 3,
                timeLeft = 120,
                initialTime = 120
            ),
            onToggle = {},
            onReset = {},
            onRecord = {},
            onSaveToNote = {},
            onSettingsClick = {}
        )
    }
}