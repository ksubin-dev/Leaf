package com.leafy.features.mypage.ui.Analysis

import com.subin.leafy.domain.model.UserAnalysis

data class AnalysisUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val analysisData: UserAnalysis? = null,
    // 필요시 차트 애니메이션 상태 등을 추가할 수 있음
    val isChartVisible: Boolean = false
)