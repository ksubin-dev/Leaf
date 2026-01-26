package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.remote.HomeDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.HomeContent
import com.subin.leafy.domain.repository.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val homeDataSource: HomeDataSource
) : HomeRepository {

    override suspend fun getHomeContent(): DataResourceResult<HomeContent> {
        return homeDataSource.getHomeContent()
    }
}