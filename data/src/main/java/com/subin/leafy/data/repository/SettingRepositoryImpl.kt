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
) : SettingRepository {

    override fun getAppSettings(): Flow<AppSettings> {
        return localSettingDataSource.getAppSettingsFlow()
    }

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

    override suspend fun setNotificationAgreed(isAgreed: Boolean): DataResourceResult<Unit> {
        return try {
            localSettingDataSource.setNotificationAgreed(isAgreed)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

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