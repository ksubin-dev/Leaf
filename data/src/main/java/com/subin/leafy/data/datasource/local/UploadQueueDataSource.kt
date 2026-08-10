package com.subin.leafy.data.datasource.local

import com.subin.leafy.domain.model.UploadQueue
import com.subin.leafy.domain.model.UploadStatus
import com.subin.leafy.domain.model.UploadTargetType
import kotlinx.coroutines.flow.Flow

interface UploadQueueDataSource {
    fun observeById(id: String): Flow<UploadQueue?>
    fun observeLatestVisible(): Flow<UploadQueue?>
    suspend fun getByTarget(targetType: UploadTargetType, targetId: String): UploadQueue?
    suspend fun upsert(queue: UploadQueue)
    suspend fun updateStatus(
        id: String,
        status: UploadStatus,
        attempt: Int = 0,
        message: String? = null,
        lastError: String? = null
    )
    suspend fun delete(id: String)
}
