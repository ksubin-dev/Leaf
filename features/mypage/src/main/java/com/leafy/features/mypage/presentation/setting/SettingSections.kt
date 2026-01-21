package com.leafy.features.mypage.presentation.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.leafy.features.mypage.presentation.setting.component.SettingsInfoItem
import com.leafy.features.mypage.presentation.setting.component.SettingsSectionTitle
import com.leafy.features.mypage.presentation.setting.component.SettingsSwitchItem
import com.leafy.features.mypage.presentation.setting.component.SettingsTextItem

@Composable
fun AppSettingsSection(
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
fun TimerSettingsSection(
    isVibOn: Boolean,
    isSoundOn: Boolean,
    isScreenOn: Boolean,
    onToggleVib: (Boolean) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onToggleScreen: (Boolean) -> Unit
) {
    Column {
        SettingsSectionTitle("타이머 설정")
        SettingsSwitchItem(
            title = "진동 알림",
            checked = isVibOn,
            onCheckedChange = onToggleVib
        )
        SettingsSwitchItem(
            title = "소리 알림",
            checked = isSoundOn,
            onCheckedChange = onToggleSound
        )
        SettingsSwitchItem(
            title = "화면 켜짐 유지",
            checked = isScreenOn,
            onCheckedChange = onToggleScreen
        )
    }
}

@Composable
fun AccountSettingsSection(
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
fun InfoSettingsSection(
    appVersion: String
) {
    Column {
        SettingsSectionTitle("정보")
        SettingsInfoItem(title = "앱 버전", value = appVersion)
    }
}