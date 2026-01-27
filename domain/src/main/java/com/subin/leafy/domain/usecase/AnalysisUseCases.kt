package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.analysis.GetUserAnalysisUseCase
import javax.inject.Inject

data class AnalysisUseCases @Inject constructor(
    val getUserAnalysis: GetUserAnalysisUseCase
)