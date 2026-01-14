package com.subin.leafy.domain.usecase.setting

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.SettingRepository

class UpdateDisplaySettingUseCase(
    private val settingRepository: SettingRepository
) {
    suspend fun setDarkMode(isDark: Boolean): DataResourceResult<Unit> {
        return settingRepository.setDarkMode(isDark)
    }

    // 언어 설정 (빈 값 체크)
    suspend fun setLanguage(langCode: String): DataResourceResult<Unit> {
        if (langCode.isBlank()) {
            return DataResourceResult.Failure(Exception("언어 코드가 올바르지 않습니다."))
        }
        // 예: 지원하지 않는 언어 코드인지 추가 검사 가능 (현재는 공백만 체크)

        return settingRepository.setLanguage(langCode)
    }
}