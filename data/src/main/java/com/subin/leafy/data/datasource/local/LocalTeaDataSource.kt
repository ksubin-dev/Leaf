package com.subin.leafy.data.datasource.local

import com.subin.leafy.domain.model.TeaItem
import kotlinx.coroutines.flow.Flow

interface LocalTeaDataSource {

    fun getTeasFlow(ownerId: String): Flow<List<TeaItem>>

    fun searchTeas(ownerId: String, query: String): Flow<List<TeaItem>>

    fun getTeaCountFlow(ownerId: String): Flow<Int>

    suspend fun getTea(id: String): TeaItem?

    suspend fun insertTea(tea: TeaItem)

    suspend fun insertTeas(teas: List<TeaItem>)

    suspend fun deleteTea(id: String)

    suspend fun deleteMyAllTeas(ownerId: String)
}