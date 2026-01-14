package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.HomeContent

interface HomeRepository {
    suspend fun getHomeContent(): DataResourceResult<HomeContent>
}