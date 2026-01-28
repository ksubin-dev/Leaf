package com.subin.leafy.domain.usecase.setting

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.SettingRepository
import javax.inject.Inject

class UpdateDisplaySettingUseCase @Inject constructor(
    private val settingRepository: SettingRepository
) {
    suspend fun setDarkMode(isDark: Boolean): DataResourceResult<Unit> {
        return settingRepository.setDarkMode(isDark)
    }

    suspend fun setLanguage(langCode: String): DataResourceResult<Unit> {
        if (langCode.isBlank()) {
            return DataResourceResult.Failure(Exception("언어 코드가 올바르지 않습니다."))
        }

        return settingRepository.setLanguage(langCode)
    }
}