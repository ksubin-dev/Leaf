package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.UserDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.UserStats
import com.subin.leafy.domain.repository.UserStatsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class UserStatsRepositoryImpl(
    private val dataSource: UserDataSource
) : UserStatsRepository {

    override fun getUserStats(userId: String): Flow<DataResourceResult<UserStats>> = flow {
        emit(dataSource.getUserStats(userId))
    }.onStart {
        emit(DataResourceResult.Loading)
    }.catch { e ->
        emit(DataResourceResult.Failure(e))
    }.flowOn(Dispatchers.IO)
}