package com.subin.leafy.domain.usecase.setting

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.SettingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ManageLoginSettingUseCase(
    private val settingRepository: SettingRepository
) {

    suspend fun setAutoLogin(enabled: Boolean): DataResourceResult<Unit> {
        return settingRepository.setAutoLogin(enabled)
    }


    fun getAutoLogin(): Flow<Boolean> {
        return settingRepository.getAppSettings()
            .map { settings ->
                settings.autoLogin
            }.distinctUntilChanged()
    }


    suspend fun saveEmail(email: String): DataResourceResult<Unit> {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isBlank()) {
            return DataResourceResult.Failure(Exception("이메일을 입력해주세요."))
        }
        if (!trimmedEmail.contains("@")) {
            return DataResourceResult.Failure(Exception("유효하지 않은 이메일 형식입니다."))
        }

        return settingRepository.saveLastLoginEmail(trimmedEmail)
    }


    suspend fun getLastEmail(): DataResourceResult<String?> {
        return settingRepository.getLastLoginEmail()
    }
}