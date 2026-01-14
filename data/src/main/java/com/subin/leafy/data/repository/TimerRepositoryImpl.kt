package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.TimerDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.TimerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.catch

class TimerRepositoryImpl(
    private val dataSource: TimerDataSource
) : TimerRepository {

    override fun getPresets() = flow {
        emit(DataResourceResult.Loading)
        delay(300) // 가짜 로딩감
        emit(dataSource.getPresets())
    }.catch {
        emit(DataResourceResult.Failure(it))
    }.flowOn(Dispatchers.IO)
}