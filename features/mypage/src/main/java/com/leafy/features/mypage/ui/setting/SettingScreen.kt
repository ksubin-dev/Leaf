package com.leafy.features.mypage.ui.setting

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
import com.leafy.features.mypage.ui.setting.component.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    viewModel: SettingViewModel,
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.logoutSuccess, uiState.deleteSuccess) {
        if (uiState.logoutSuccess || uiState.deleteSuccess) {
            onLogoutSuccess()
        }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("설정", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
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
@Composable
private fun AppSettingsSection(
    isNotiEnabled: Boolean,
    isAutoLoginEnabled: Boolean,
    onToggleNoti: (Boolean) -> Unit,
    onToggleAutoLogin: (Boolean) -> Unit
) {
    Column {
        SettingsSectionTitle("앱 설정")
        SettingsSwitchItem(
            title = "알림 설정",
            checked = isNotiEnabled,
            onCheckedChange = onToggleNoti
        )
        SettingsSwitchItem(
            title = "자동 로그인",
            checked = isAutoLoginEnabled,
            onCheckedChange = onToggleAutoLogin
        )
    }
}

@Composable
private fun AccountSettingsSection(
    onLogoutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit
) {
    Column {
        SettingsSectionTitle("계정")
        SettingsTextItem(
            title = "로그아웃",
            onClick = onLogoutClick
        )
        SettingsTextItem(
            title = "회원 탈퇴",
            color = MaterialTheme.colorScheme.error,
            onClick = onDeleteAccountClick
        )
    }
}

@Composable
private fun InfoSettingsSection(
    appVersion: String
) {
    Column {
        SettingsSectionTitle("정보")
        SettingsInfoItem(title = "앱 버전", value = appVersion)
    }
}