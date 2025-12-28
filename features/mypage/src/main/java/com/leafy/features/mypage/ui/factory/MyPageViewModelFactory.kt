package com.leafy.features.mypage.ui.factory

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leafy.features.mypage.ui.MyPageViewModel
import com.leafy.shared.di.ApplicationContainer


class MyPageViewModelFactory(
    private val container: ApplicationContainer
) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MyPageViewModel(
                getCurrentUserIdUseCase = container.userUseCases.getCurrentUserId,
                getUserUseCase = container.userUseCases.getUser,
                getUserStatsUseCase = container.userUseCases.getUserStats,
                getMonthlyRecordsUseCase = container.noteUseCases.getMonthlyRecords,
                getRecordByDateUseCase = container.noteUseCases.getRecordByDate
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}