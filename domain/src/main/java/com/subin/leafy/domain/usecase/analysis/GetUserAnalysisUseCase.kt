package com.subin.leafy.domain.usecase.analysis

import com.subin.leafy.domain.model.UserAnalysis
import com.subin.leafy.domain.repository.AnalysisRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserAnalysisUseCase @Inject constructor(
    private val analysisRepository: AnalysisRepository
) {
    operator fun invoke(ownerId: String): Flow<UserAnalysis> {
        return analysisRepository.getUserAnalysisFlow(ownerId)
    }
}