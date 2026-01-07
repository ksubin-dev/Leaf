package com.subin.leafy.data.datasource.remote

import kotlinx.coroutines.flow.Flow

interface SettingDataSource {
    fun getThemeMode(): Flow<Boolean> // true: 다크, false: 라이트
    suspend fun setThemeMode(isDark: Boolean)

    fun getNotificationAgreed(): Flow<Boolean>
    suspend fun setNotificationAgreed(agreed: Boolean)

    fun getLanguage(): Flow<String>
    suspend fun setLanguage(langCode: String)
}