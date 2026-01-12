package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.setting.*

data class SettingUseCases(
    val getAppSettings: GetAppSettingsUseCase,
    val updateDisplaySetting: UpdateDisplaySettingUseCase,
    val updateNotificationSetting: UpdateNotificationSettingUseCase,
    val manageLoginSetting: ManageLoginSettingUseCase,
    val clearAppSettings: ClearAppSettingsUseCase
)