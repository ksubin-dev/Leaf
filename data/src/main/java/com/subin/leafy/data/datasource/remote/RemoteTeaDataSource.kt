package com.subin.leafy.data.datasource.remote

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaItem

interface RemoteTeaDataSource {

    suspend fun getMyBackupTeas(userId: String): DataResourceResult<List<TeaItem>>

    suspend fun saveTea(tea: TeaItem): DataResourceResult<Unit>

    suspend fun deleteTea(teaId: String): DataResourceResult<Unit>
}