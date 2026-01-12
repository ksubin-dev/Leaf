package com.subin.leafy.data.datasource.local

import com.subin.leafy.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface LocalSettingDataSource {

    // --- 1. 앱 설정 (DataStore) ---

    // 설정값 전체 스트림 (UI에서 구독)
    fun getAppSettingsFlow(): Flow<AppSettings>

    // 테마 변경 (Dark, Light, System)
    suspend fun setDarkMode(isDark: Boolean)

    // 언어 변경
    suspend fun setLanguage(langCode: String)

    // 알림 수신 동의 여부 (내 폰 설정)
    suspend fun setNotificationAgreed(agreed: Boolean)


    // --- 2. 로그인 편의 기능 (DataStore) ---

    // 자동 로그인 여부
    suspend fun updateAutoLogin(enabled: Boolean)

    // 마지막 로그인 이메일 (로그인 화면에 미리 채워주기용)
    suspend fun saveLastLoginEmail(email: String)
    suspend fun getLastLoginEmail(): String?

    // 설정 전체 초기화 (로그아웃 시)
    suspend fun clearSettings()
}