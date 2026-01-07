package com.subin.leafy.data.datasource.local

import com.subin.leafy.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingDataSource {
    // 설정값 전체 가져오기
    fun getSettings(): Flow<AppSettings>

    // 특정 설정값 수정 (로그인 상태 유지 토글)
    suspend fun updateAutoLogin(enabled: Boolean)

    // 마지막 로그인 이메일 저장
    suspend fun saveLastLoginEmail(email: String)
}