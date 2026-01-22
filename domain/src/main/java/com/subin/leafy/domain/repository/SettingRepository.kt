package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingRepository {

    fun getAppSettings(): Flow<AppSettings>

    suspend fun setDarkMode(isDark: Boolean): DataResourceResult<Unit>
    suspend fun setLanguage(langCode: String): DataResourceResult<Unit>
    suspend fun setNotificationAgreed(isAgreed: Boolean): DataResourceResult<Unit>

    suspend fun setAutoLogin(enabled: Boolean): DataResourceResult<Unit>
    suspend fun saveLastLoginEmail(email: String): DataResourceResult<Unit>

    suspend fun getLastLoginEmail(): DataResourceResult<String?>

    suspend fun clearSettings(): DataResourceResult<Unit>
}