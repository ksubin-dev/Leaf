package com.subin.leafy.data.datasource.local

import com.subin.leafy.domain.model.UserAnalysis
import kotlinx.coroutines.flow.Flow

interface AnalysisDataSource {
    fun getUserAnalysis(ownerId: String): Flow<UserAnalysis>
}