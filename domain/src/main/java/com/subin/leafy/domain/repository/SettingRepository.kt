package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingRepository {

    // 1. 설정값 전체 구독
    fun getAppSettings(): Flow<AppSettings>

    // 2. 개별 설정 변경
    suspend fun setDarkMode(isDark: Boolean): DataResourceResult<Unit>
    suspend fun setLanguage(langCode: String): DataResourceResult<Unit>

    // 알림 설정 변경
    suspend fun setNotificationAgreed(isAgreed: Boolean): DataResourceResult<Unit>

    // 3. 로그인 관련
    suspend fun setAutoLogin(enabled: Boolean): DataResourceResult<Unit>
    suspend fun saveLastLoginEmail(email: String): DataResourceResult<Unit>

    suspend fun getLastLoginEmail(): DataResourceResult<String?>

    suspend fun clearSettings(): DataResourceResult<Unit>
}