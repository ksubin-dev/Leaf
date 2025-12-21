package com.leafy.features.timer.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leafy.features.timer.ui.TimerViewModel
import com.subin.leafy.domain.usecase.TimerUseCases

class TimerViewModelFactory(
    private val timerUseCases: TimerUseCases
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel(timerUseCases) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}