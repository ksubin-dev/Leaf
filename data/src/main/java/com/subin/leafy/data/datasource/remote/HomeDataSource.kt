package com.subin.leafy.data.datasource.remote

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.HomeContent

interface HomeDataSource {
    suspend fun getHomeContent(): DataResourceResult<HomeContent>
}