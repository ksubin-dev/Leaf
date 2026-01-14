package com.subin.leafy.domain.usecase.setting

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.SettingRepository

class ClearAppSettingsUseCase(
    private val settingRepository: SettingRepository
) {
    suspend operator fun invoke(): DataResourceResult<Unit> {
        return settingRepository.clearSettings()
    }
}