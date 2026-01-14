package com.subin.leafy.data.datasource.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// 1. 기존: 앱의 일반적인 설정 (로그인 여부, 다크모드 등)
// 파일명: leafy_preferences.preferences_pb
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "leafy_preferences")

// 2. 타이머 관련 전용 설정 (타이머 프리셋, 최근 레시피 등)
// 파일명: timer_settings.preferences_pb
val Context.timerDataStore: DataStore<Preferences> by preferencesDataStore(name = "timer_settings")