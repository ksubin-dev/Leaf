package com.subin.leafy.domain.usecase.analysis

import com.subin.leafy.domain.model.UserAnalysis
import com.subin.leafy.domain.repository.AnalysisRepository
import kotlinx.coroutines.flow.Flow

class GetUserAnalysisUseCase(
    private val analysisRepository: AnalysisRepository
) {
    operator fun invoke(): Flow<UserAnalysis> {
        return analysisRepository.getUserAnalysisFlow()
    }
}