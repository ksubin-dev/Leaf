package com.subin.leafy

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.navigation.MainNavigationRoute
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.AuthUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.SettingUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val userUseCases: UserUseCases,
    private val authUseCases: AuthUseCases,
    private val settingUseCases: SettingUseCases
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
                Log.d("SYNC_LOG", "자동 로그인 유저 발견 -> 동기화 시도")

                withTimeoutOrNull(2500L) {
                    launch(Dispatchers.IO) {
                        try {
                            noteUseCases.syncNotes()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.join()
                }

                _startDestination.value = MainNavigationRoute.HomeTab
            } else {
                if (userResult is DataResourceResult.Success) {
                    Log.d("SYNC_LOG", "로그인 정보는 있으나 자동 로그인이 꺼져있음 -> 로그아웃 처리")
                    authUseCases.logout()
                }

                _startDestination.value = MainNavigationRoute.Auth
            }

            val elapsedTime = System.currentTimeMillis() - startTime
            val minSplashTime = 2000L
            if (elapsedTime < minSplashTime) {
                delay(minSplashTime - elapsedTime)
            }

            _isSplashLoading.value = false
        }
    }
}