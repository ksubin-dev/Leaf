package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.TimerDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.TimerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart

class TimerRepositoryImpl(
    private val dataSource: TimerDataSource
) : TimerRepository {

    override fun getPresets() = flow {
        emit(dataSource.getPresets())
    }.onStart {
        emit(DataResourceResult.Loading)
    }.catch { e ->
        emit(DataResourceResult.Failure(e))
    }.flowOn(Dispatchers.IO)
}