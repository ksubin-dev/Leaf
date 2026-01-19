package com.subin.leafy.domain.repository

import com.subin.leafy.domain.model.UserAnalysis
import kotlinx.coroutines.flow.Flow

interface AnalysisRepository {
    fun getUserAnalysisFlow(ownerId: String): Flow<UserAnalysis>
}