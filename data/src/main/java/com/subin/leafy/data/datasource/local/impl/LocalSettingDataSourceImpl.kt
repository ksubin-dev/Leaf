package com.subin.leafy.data.datasource.local.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.subin.leafy.data.datasource.local.LocalSettingDataSource
import com.subin.leafy.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class LocalSettingDataSourceImpl @Inject constructor(
    @Named("settings") private val dataStore: DataStore<Preferences>
) : LocalSettingDataSource {

    companion object {
        private val KEY_IS_DARK_THEME = booleanPreferencesKey("pref_is_dark_theme")
        private val KEY_LANGUAGE = stringPreferencesKey("pref_language")
        private val KEY_NOTI_AGREED = booleanPreferencesKey("pref_noti_agreed")
        private val KEY_AUTO_LOGIN = booleanPreferencesKey("pref_auto_login")
        private val KEY_LAST_EMAIL = stringPreferencesKey("pref_last_email")
    }


    override fun getAppSettingsFlow(): Flow<AppSettings> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { prefs ->
                AppSettings(
                    isDarkTheme = prefs[KEY_IS_DARK_THEME] ?: false,
                    language = prefs[KEY_LANGUAGE] ?: "ko",
                    isNotificationAgreed = prefs[KEY_NOTI_AGREED] ?: false,
                    autoLogin = prefs[KEY_AUTO_LOGIN] ?: false
                )
            }
    }

    override suspend fun setDarkMode(isDark: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_IS_DARK_THEME] = isDark
        }
    }

    override suspend fun setLanguage(langCode: String) {
        dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = langCode
        }
    }

    override suspend fun setNotificationAgreed(agreed: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_NOTI_AGREED] = agreed
        }
    }


    override suspend fun updateAutoLogin(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_AUTO_LOGIN] = enabled
        }
    }

    override suspend fun saveLastLoginEmail(email: String) {
        dataStore.edit { prefs ->
            prefs[KEY_LAST_EMAIL] = email
        }
    }

    override suspend fun getLastLoginEmail(): String? {
        val prefs = dataStore.data.firstOrNull() ?: return null
        return prefs[KEY_LAST_EMAIL]
    }


    override suspend fun clearSettings() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_IS_DARK_THEME)
            prefs.remove(KEY_NOTI_AGREED)
            prefs.remove(KEY_AUTO_LOGIN)

        }
    }
}