package com.leafy.features.mypage.presentation.analysis

import com.subin.leafy.domain.model.UserAnalysis

data class AnalysisUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val analysisData: UserAnalysis? = null,
    val isChartVisible: Boolean = false
)