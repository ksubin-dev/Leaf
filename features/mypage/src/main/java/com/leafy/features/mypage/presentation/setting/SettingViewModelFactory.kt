package com.leafy.features.mypage.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.subin.leafy.domain.usecase.AuthUseCases
import com.subin.leafy.domain.usecase.SettingUseCases
import com.subin.leafy.domain.usecase.TimerUseCases
import com.subin.leafy.domain.usecase.UserUseCases

class SettingViewModelFactory(
    private val settingUseCases: SettingUseCases,
    private val authUseCases: AuthUseCases,
    private val userUseCases: UserUseCases,
    private val timerUseCases: TimerUseCases
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingViewModel(
            settingUseCases,
            authUseCases,
            userUseCases,
            timerUseCases
        ) as T
    }
}