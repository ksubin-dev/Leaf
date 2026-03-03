package com.subin.leafy.data.datasource.local

import com.subin.leafy.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface LocalSettingDataSource {

    fun getAppSettingsFlow(): Flow<AppSettings>

    suspend fun setDarkMode(isDark: Boolean)

    suspend fun setLanguage(langCode: String)

    suspend fun setNotificationAgreed(agreed: Boolean)


    suspend fun updateAutoLogin(enabled: Boolean)

    suspend fun saveLastLoginEmail(email: String)
    suspend fun getLastLoginEmail(): String?

    suspend fun clearSettings()
}