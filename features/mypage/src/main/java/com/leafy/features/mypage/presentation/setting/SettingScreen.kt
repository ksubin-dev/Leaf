package com.leafy.features.mypage.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.mypage.presentation.setting.component.DeleteAccountDialog
import com.leafy.features.mypage.presentation.setting.component.LogoutDialog
import com.leafy.shared.common.singleClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    viewModel: SettingViewModel,
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.logoutSuccess, uiState.deleteSuccess) {
        if (uiState.logoutSuccess || uiState.deleteSuccess) {
            onLogoutSuccess()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.messageShown()
        }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("설정", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = singleClick { onBackClick() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            AppSettingsSection(
                isNotiEnabled = uiState.isNotificationEnabled,
                isAutoLoginEnabled = uiState.isAutoLoginEnabled,
                onToggleNoti = viewModel::toggleNotification,
                onToggleAutoLogin = viewModel::toggleAutoLogin
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

            TimerSettingsSection(
                isVibOn = uiState.isTimerVibrationOn,
                isSoundOn = uiState.isTimerSoundOn,
                isScreenOn = uiState.isTimerScreenOn,
                onToggleVib = viewModel::toggleTimerVibration,
                onToggleSound = viewModel::toggleTimerSound,
                onToggleScreen = viewModel::toggleTimerScreenOn
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

            AccountSettingsSection(
                onLogoutClick = { showLogoutDialog = true },
                onDeleteAccountClick = { showDeleteDialog = true }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp)

            InfoSettingsSection(appVersion = uiState.appVersion)
        }
    }

    if (showLogoutDialog) {
        LogoutDialog(
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    if (showDeleteDialog) {
        DeleteAccountDialog(
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteAccount()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}