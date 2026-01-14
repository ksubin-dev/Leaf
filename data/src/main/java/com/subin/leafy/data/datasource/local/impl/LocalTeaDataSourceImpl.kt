package com.subin.leafy.data.datasource.local.impl

import com.subin.leafy.data.datasource.local.LocalTeaDataSource
import com.subin.leafy.data.datasource.local.room.dao.TeaDao
import com.subin.leafy.data.mapper.toEntity
import com.subin.leafy.data.mapper.toTeaDomainList
import com.subin.leafy.data.mapper.toDomain
import com.subin.leafy.domain.model.TeaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalTeaDataSourceImpl(
    private val teaDao: TeaDao
) : LocalTeaDataSource {

    override fun getTeasFlow(): Flow<List<TeaItem>> {
        return teaDao.getAllTeas().map { it.toTeaDomainList() }
    }

    override fun searchTeas(query: String): Flow<List<TeaItem>> {
        return teaDao.searchTeas(query).map { it.toTeaDomainList() }
    }

    override fun getTeaCountFlow(): Flow<Int> {
        return teaDao.getTeaCount()
    }

    override suspend fun getTea(id: String): TeaItem? {
        return teaDao.getTeaById(id)?.toDomain()
    }

    override suspend fun insertTea(tea: TeaItem) {
        teaDao.insertTea(tea.toEntity())
    }

    override suspend fun deleteTea(id: String) {
        teaDao.deleteTea(id)
    }
}