package com.subin.leafy.data.datasource.local.impl

import com.subin.leafy.data.datasource.local.UploadQueueDataSource
import com.subin.leafy.data.datasource.local.room.dao.UploadQueueDao
import com.subin.leafy.data.mapper.toDomain
import com.subin.leafy.data.mapper.toEntity
import com.subin.leafy.domain.model.UploadQueue
import com.subin.leafy.domain.model.UploadStatus
import com.subin.leafy.domain.model.UploadTargetType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UploadQueueDataSourceImpl @Inject constructor(
    private val dao: UploadQueueDao
) : UploadQueueDataSource {

    override fun observeById(id: String): Flow<UploadQueue?> {
        return dao.observeById(id).map { it?.toDomain() }
    }

    override fun observeLatestVisible(): Flow<UploadQueue?> {
        return dao.observeLatestVisible().map { it?.toDomain() }
    }

    override suspend fun getByTarget(targetType: UploadTargetType, targetId: String): UploadQueue? {
        return dao.getByTarget(targetType.name, targetId)?.toDomain()
    }

    override suspend fun upsert(queue: UploadQueue) {
        dao.upsert(queue.toEntity())
    }

    override suspend fun updateStatus(
        id: String,
        status: UploadStatus,
        attempt: Int,
        message: String?,
        lastError: String?
    ) {
        dao.updateStatus(
            id = id,
            status = status.name,
            attempt = attempt,
            message = message,
            lastError = lastError,
            updatedAt = System.currentTimeMillis()
        )
    }

    override suspend fun delete(id: String) {
        dao.delete(id)
    }
}
