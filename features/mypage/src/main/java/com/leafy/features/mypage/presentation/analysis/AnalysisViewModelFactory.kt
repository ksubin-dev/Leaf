package com.leafy.features.mypage.presentation.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.subin.leafy.domain.usecase.AnalysisUseCases
import com.subin.leafy.domain.usecase.UserUseCases

class AnalysisViewModelFactory(
    private val analysisUseCases: AnalysisUseCases,
    private val userUseCases: UserUseCases
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AnalysisViewModel(analysisUseCases, userUseCases) as T
    }
}