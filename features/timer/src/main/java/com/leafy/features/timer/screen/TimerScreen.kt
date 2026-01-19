package com.leafy.features.timer.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.leafy.features.timer.ui.TimerStatus
import com.leafy.features.timer.ui.TimerUiState
import com.leafy.features.timer.ui.TimerViewModel
import com.leafy.features.timer.ui.components.*
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafyDialog
import com.leafy.shared.ui.theme.LeafyTheme
import com.leafy.shared.utils.DeviceFeedbackUtils.triggerNotificationSound
import com.leafy.shared.utils.DeviceFeedbackUtils.triggerVibration
import com.subin.leafy.domain.model.InfusionRecord
import com.subin.leafy.domain.model.TeaType

@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    onBackClick: () -> Unit,
    onNavigateToNote: (String) -> Unit,
    onNavigateToPresetList: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showExitDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = uiState.status == TimerStatus.RUNNING) {
        showExitDialog = true
    }

    LaunchedEffect(uiState.status, uiState.isAlarmFired) {
        if (uiState.status == TimerStatus.COMPLETED && !uiState.isAlarmFired) {

            if (uiState.settings.isVibrationOn) {
                triggerVibration(context)
            }

            if (uiState.settings.isSoundOn) {
                triggerNotificationSound(context)
            }
            viewModel.markAlarmAsFired()
        }
    }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.messageShown()
        }
    }

    if (showExitDialog) {
        LeafyDialog(
            onDismissRequest = { showExitDialog = false },
            title = "타이머 중단",
            text = "타이머가 실행 중입니다. 정말 나갈까요?",
            confirmText = "나가기",
            dismissText = "계속하기",
            onConfirmClick = {
                showExitDialog = false
                viewModel.resetTimer()
                onBackClick()
            }
        )
    }

    TimerScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = {
            if (uiState.status == TimerStatus.RUNNING) {
                showExitDialog = true
            } else {
                onBackClick()
            }
        },
        onNavigateToNoteClick = {
            val navArgs = viewModel.getBrewingDataJson()
            onNavigateToNote(navArgs)
        },
        onNavigateToPresetList = onNavigateToPresetList,
        onToggleTimer = {
            if (uiState.status == TimerStatus.RUNNING) viewModel.pauseTimer()
            else viewModel.startTimer()
        },
        onResetTimer = viewModel::resetTimer,
        onDeleteRecord = viewModel::deleteInfusionRecord
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreenContent(
    uiState: TimerUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onNavigateToNoteClick: () -> Unit,
    onNavigateToPresetList: () -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onDeleteRecord: (Int) -> Unit
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "우림 타이머",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = singleClick { onBackClick() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState.infusionRecords.isNotEmpty()) {
                Surface(
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Button(
                        onClick = singleClick { onNavigateToNoteClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "이 기록으로 시음 노트 쓰기",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = if (uiState.infusionRecords.isNotEmpty()) 20.dp else 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    CurrentPresetCard(
                        presetName = uiState.currentTeaName,
                        targetTemp = uiState.targetTemperature,
                        targetTime = uiState.targetTimeSeconds,
                        teaType = uiState.selectedTeaType,
                        onClickSettings = onNavigateToPresetList
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    CircularTimerDisplay(
                        progress = uiState.progress,
                        remainingSeconds = uiState.remainingSeconds,
                        currentStatusText = when (uiState.status) {
                            TimerStatus.IDLE -> "준비 완료"
                            TimerStatus.RUNNING -> "우리는 중..."
                            TimerStatus.PAUSED -> "일시 정지"
                            TimerStatus.COMPLETED -> "우림 완료!"
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    TimerControls(
                        isRunning = uiState.status == TimerStatus.RUNNING,
                        onToggleTimer = onToggleTimer,
                        onResetTimer = onResetTimer
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            if (uiState.infusionRecords.isNotEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "이번 세션 기록",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                items(
                    items = uiState.infusionRecords.reversed(),
                    key = { it.count }
                ) { record ->
                    InfusionRecordItem(
                        count = record.count,
                        timeSeconds = record.timeSeconds,
                        temp = record.waterTemp,
                        onDelete = { onDeleteRecord(record.count) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "타이머 실행 중 화면")
@Composable
fun TimerScreenPreview() {
    val mockRecords = listOf(
        InfusionRecord(count = 1, timeSeconds = 180, waterTemp = 90),
        InfusionRecord(count = 2, timeSeconds = 60, waterTemp = 95)
    )

    val mockState = TimerUiState(
        status = TimerStatus.RUNNING,
        remainingSeconds = 120,
        progress = 0.6f,
        targetTimeSeconds = 200,
        targetTemperature = 90,
        selectedTeaType = TeaType.GREEN,
        infusionRecords = mockRecords
    )

    LeafyTheme {
        TimerScreenContent(
            uiState = mockState,
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateToNoteClick = {},
            onToggleTimer = {},
            onResetTimer = {},
            onNavigateToPresetList = {},
            onDeleteRecord = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "타이머 대기 화면 (기록 없음)")
@Composable
fun TimerScreenIdlePreview() {
    val mockState = TimerUiState(
        status = TimerStatus.IDLE,
        remainingSeconds = 180,
        progress = 1.0f,
        infusionRecords = emptyList()
    )

    LeafyTheme {
        TimerScreenContent(
            uiState = mockState,
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateToNoteClick = {},
            onToggleTimer = {},
            onResetTimer = {},
            onNavigateToPresetList = {},
            onDeleteRecord = {},
            onBackClick = {}
        )
    }
}