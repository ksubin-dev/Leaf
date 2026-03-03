package com.leafy.features.mypage.presentation.setting

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.leafy.features.mypage.presentation.setting.component.DeleteAccountDialog
import com.leafy.features.mypage.presentation.setting.component.LogoutDialog
import com.leafy.shared.R
import com.leafy.shared.common.singleClick
import com.leafy.shared.ui.component.LeafyDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current // 생명주기 관찰용

    var showPermissionDialog by remember { mutableStateOf(false) }

    var isWaitingForPermissionReturn by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && isWaitingForPermissionReturn) {
                isWaitingForPermissionReturn = false

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (hasPermission) {
                        viewModel.toggleNotification(true)
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleNotification(true)
        } else {
            showPermissionDialog = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is SettingSideEffect.LogoutSuccess,
                is SettingSideEffect.DeleteAccountSuccess -> onLogoutSuccess()
                is SettingSideEffect.ShowToast -> {
                    Toast.makeText(context, effect.message.asString(context), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.title_settings), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = singleClick { onBackClick() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
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
                onToggleAutoLogin = viewModel::toggleAutoLogin,
                onToggleNoti = { isChecked ->
                    if (isChecked) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            val hasPermission = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.POST_NOTIFICATIONS
                            ) == PackageManager.PERMISSION_GRANTED

                            if (hasPermission) {
                                viewModel.toggleNotification(true)
                            } else {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        } else {
                            viewModel.toggleNotification(true)
                        }
                    } else {
                        viewModel.toggleNotification(false)
                    }
                }
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

    if (showPermissionDialog) {
        LeafyDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = "알림 권한 설정",
            text = "기기 알림이 꺼져있어 알림을 받을 수 없습니다.\n설정 화면으로 이동하여 알림을 켜주시겠습니까?",
            confirmText = "설정으로 이동",
            dismissText = "취소",
            onConfirmClick = {
                showPermissionDialog = false
                isWaitingForPermissionReturn = true

                val intent = Intent().apply {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    } else {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.parse("package:${context.packageName}")
                    }
                }
                context.startActivity(intent)
            }
        )
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