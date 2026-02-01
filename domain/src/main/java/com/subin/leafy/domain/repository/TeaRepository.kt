package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaItem
import kotlinx.coroutines.flow.Flow

interface TeaRepository {

    fun getTeasFlow(): Flow<List<TeaItem>>

    fun searchTeas(query: String): Flow<List<TeaItem>>

    fun getTeaCountFlow(): Flow<Int>

    suspend fun getTeaDetail(id: String): TeaItem?

    suspend fun saveTea(tea: TeaItem): DataResourceResult<Unit>

    suspend fun deleteTea(id: String): DataResourceResult<Unit>

    suspend fun toggleFavorite(teaId: String): DataResourceResult<Unit>

    suspend fun syncTeas(): DataResourceResult<Unit>

    suspend fun scheduleTeaUpload(tea: TeaItem, imageUriString: String?)
}