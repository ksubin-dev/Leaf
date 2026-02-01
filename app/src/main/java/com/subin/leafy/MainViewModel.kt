package com.subin.leafy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.leafy.shared.navigation.MainNavigationRoute
import com.subin.leafy.data.worker.SyncWorker
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.AuthUseCases
import com.subin.leafy.domain.usecase.SettingUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val authUseCases: AuthUseCases,
    private val settingUseCases: SettingUseCases,
    private val workManager: WorkManager
) : ViewModel() {

    private val _isSplashLoading = MutableStateFlow(true)
    val isSplashLoading = _isSplashLoading.asStateFlow()

    private val _startDestination = MutableStateFlow<Any?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        initializeApp()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()

            val userResult = userUseCases.getCurrentUserId()
            val isAutoLoginEnabled = settingUseCases.manageLoginSetting.getAutoLogin().first()

            if (userResult is DataResourceResult.Success && isAutoLoginEnabled) {
                Log.d("SYNC_LOG", "자동 로그인 유저 발견 -> WorkManager 동기화 예약 (기다리지 않음)")

                val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .build()

                workManager.enqueue(syncRequest)

                _startDestination.value = MainNavigationRoute.HomeTab
            }
            else {
                if (userResult is DataResourceResult.Success) {
                    Log.d("SYNC_LOG", "자동 로그인 꺼짐 -> 로그아웃 처리")
                    authUseCases.logout()
                }
                _startDestination.value = MainNavigationRoute.Auth
            }

            val elapsedTime = System.currentTimeMillis() - startTime
            val minSplashTime = 1500L

            if (elapsedTime < minSplashTime) {
                delay(minSplashTime - elapsedTime)
            }

            _isSplashLoading.value = false
        }
    }
}