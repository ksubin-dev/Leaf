package com.subin.leafy.domain.usecase.setting

import com.subin.leafy.domain.model.AppSettings
import com.subin.leafy.domain.repository.SettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppSettingsUseCase @Inject constructor(
    private val settingRepository: SettingRepository
) {
    operator fun invoke(): Flow<AppSettings> {
        return settingRepository.getAppSettings()
    }
}