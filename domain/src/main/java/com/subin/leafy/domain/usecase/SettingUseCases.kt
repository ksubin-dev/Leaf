package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.setting.*
import javax.inject.Inject

data class SettingUseCases @Inject constructor(
    val getAppSettings: GetAppSettingsUseCase,
    val updateDisplaySetting: UpdateDisplaySettingUseCase,
    val updateNotificationSetting: UpdateNotificationSettingUseCase,
    val manageLoginSetting: ManageLoginSettingUseCase,
    val clearAppSettings: ClearAppSettingsUseCase
)