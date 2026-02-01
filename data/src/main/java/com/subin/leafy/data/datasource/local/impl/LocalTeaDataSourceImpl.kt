package com.subin.leafy.data.datasource.local.impl

import com.subin.leafy.data.datasource.local.LocalTeaDataSource
import com.subin.leafy.data.datasource.local.room.dao.TeaDao
import com.subin.leafy.data.mapper.toEntity
import com.subin.leafy.data.mapper.toTeaDomainList
import com.subin.leafy.data.mapper.toDomain
import com.subin.leafy.domain.model.TeaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalTeaDataSourceImpl @Inject constructor(
    private val teaDao: TeaDao
) : LocalTeaDataSource {

    override fun getTeasFlow(ownerId: String): Flow<List<TeaItem>> {
        return teaDao.getAllTeas(ownerId).map { it.toTeaDomainList() }
    }

    override fun searchTeas(ownerId: String, query: String): Flow<List<TeaItem>> {
        return teaDao.searchTeas(ownerId, query).map { it.toTeaDomainList() }
    }

    override fun getTeaCountFlow(ownerId: String): Flow<Int> {
        return teaDao.getTeaCount(ownerId)
    }

    override suspend fun getTea(id: String): TeaItem? {
        return teaDao.getTeaById(id)?.toDomain()
    }

    override suspend fun insertTea(tea: TeaItem) {
        teaDao.insertTea(tea.toEntity())
    }

    override suspend fun insertTeas(teas: List<TeaItem>) {
        val entities = teas.map { it.toEntity() }
        teaDao.insertTeas(entities)
    }

    override suspend fun deleteTea(id: String) {
        teaDao.deleteTea(id)
    }

    override suspend fun deleteMyAllTeas(ownerId: String) {
        teaDao.deleteMyAllTeas(ownerId)
    }
}