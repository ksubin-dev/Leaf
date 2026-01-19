package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.local.AnalysisDataSource
import com.subin.leafy.domain.model.UserAnalysis
import com.subin.leafy.domain.repository.AnalysisRepository
import kotlinx.coroutines.flow.Flow

class AnalysisRepositoryImpl(
    private val analysisDataSource: AnalysisDataSource
) : AnalysisRepository {

    override fun getUserAnalysisFlow(ownerId: String): Flow<UserAnalysis> {
        return analysisDataSource.getUserAnalysis(ownerId)
    }
}