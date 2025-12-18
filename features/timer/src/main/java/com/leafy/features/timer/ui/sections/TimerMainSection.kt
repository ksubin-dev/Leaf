package com.leafy.features.timer.ui.sections

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.timer.ui.TimerUiState
import com.leafy.features.timer.ui.components.CircularTimerDisplay
import com.leafy.features.timer.ui.components.CurrentPresetCard
import com.leafy.features.timer.ui.components.TimerControls
import com.leafy.shared.ui.theme.LeafyTheme
import com.subin.leafy.domain.model.TimerPreset

@Composable
fun TimerMainSection(
    modifier: Modifier = Modifier,
    uiState: TimerUiState,
    onToggle: () -> Unit,
    onReset: () -> Unit,
    onRecord: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CurrentPresetCard(
            preset = uiState.selectedPreset,
            onClickSettings = onSettingsClick
        )

        Spacer(modifier = Modifier.height(48.dp))

        CircularTimerDisplay(
            progress = uiState.progress,
            formattedTime = uiState.formattedTime,
            currentInfusion = uiState.currentInfusion
        )

        Spacer(modifier = Modifier.height(48.dp))

        TimerControls(
            isRunning = uiState.isRunning,
            onToggleTimer = onToggle,
            onResetTimer = onReset,
            onRecordInfusion = onRecord
        )
    }
}

@Preview(showBackground = true, name = "Timer Main Section - Running")
@Composable
fun TimerMainSectionPreview() {
    LeafyTheme {
        TimerMainSection(
            modifier = Modifier.padding(20.dp),
            uiState = TimerUiState(
                selectedPreset = TimerPreset(name = "우전 녹차", temp = "80°C", leafAmount = "3g"),
                timeLeft = 45,
                initialTime = 60,
                isRunning = true,
                currentInfusion = 1
            ),
            onToggle = {},
            onReset = {},
            onRecord = {},
            onSettingsClick = {}
        )
    }
}