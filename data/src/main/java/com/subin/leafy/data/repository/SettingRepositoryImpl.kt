package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.local.LocalSettingDataSource
import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.UserDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.AppSettings
import com.subin.leafy.domain.repository.SettingRepository
import kotlinx.coroutines.flow.Flow

class SettingRepositoryImpl(
    private val localSettingDataSource: LocalSettingDataSource,
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource
) : SettingRepository {

    // 1. 전체 설정 스트림
    override fun getAppSettings(): Flow<AppSettings> {
        return localSettingDataSource.getAppSettingsFlow()
    }

    // 2. 단순 로컬 설정
    override suspend fun setDarkMode(isDark: Boolean): DataResourceResult<Unit> {
        return try {
            localSettingDataSource.setDarkMode(isDark)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun setLanguage(langCode: String): DataResourceResult<Unit> {
        return try {
            localSettingDataSource.setLanguage(langCode)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    // 3. [핵심] 알림 설정 (로컬 + 서버 동기화)
    override suspend fun setNotificationAgreed(isAgreed: Boolean): DataResourceResult<Unit> {
        return try {
            // (1) 로컬 저장 (우선 수행)
            localSettingDataSource.setNotificationAgreed(isAgreed)

            // (2) 서버 동기화 (실패해도 로컬 저장은 성공했으므로 전체 프로세스는 성공으로 간주하거나,
            // 정책에 따라 Failure로 처리할 수 있음. 여기선 '성공'으로 처리하되 서버 에러는 로그만 찍음)
            val myUid = authDataSource.getCurrentUserId()
            if (myUid != null) {
                try {
                    userDataSource.updateNotificationSetting(myUid, isAgreed)
                } catch (e: Exception) {
                    e.printStackTrace() // 서버 동기화 실패 로그
                    // 필요 시 여기에 WorkManager 작업 예약 로직 추가
                }
            }

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            // 로컬 저장 자체가 실패하면 진짜 실패
            DataResourceResult.Failure(e)
        }
    }

    // 4. 로그인 편의 기능
    override suspend fun setAutoLogin(enabled: Boolean): DataResourceResult<Unit> {
        return try {
            localSettingDataSource.updateAutoLogin(enabled)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun saveLastLoginEmail(email: String): DataResourceResult<Unit> {
        return try {
            localSettingDataSource.saveLastLoginEmail(email)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun getLastLoginEmail(): DataResourceResult<String?> {
        return try {
            val email = localSettingDataSource.getLastLoginEmail()
            DataResourceResult.Success(email)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun clearSettings(): DataResourceResult<Unit> {
        return try {
            localSettingDataSource.clearSettings()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }
}