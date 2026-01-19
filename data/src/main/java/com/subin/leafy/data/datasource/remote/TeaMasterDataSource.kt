package com.subin.leafy.data.datasource.remote

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaMaster
import kotlinx.coroutines.flow.Flow

interface TeaMasterDataSource {
    fun getRecommendedMasters(limit: Int): Flow<DataResourceResult<List<TeaMaster>>>
}