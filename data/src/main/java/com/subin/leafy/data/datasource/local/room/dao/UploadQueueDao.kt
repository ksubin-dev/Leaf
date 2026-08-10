package com.subin.leafy.data.datasource.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.subin.leafy.data.datasource.local.room.entity.UploadQueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UploadQueueDao {

    @Query("SELECT * FROM upload_queue WHERE id = :id")
    fun observeById(id: String): Flow<UploadQueueEntity?>

    @Query("SELECT * FROM upload_queue WHERE targetType = :targetType AND targetId = :targetId LIMIT 1")
    suspend fun getByTarget(targetType: String, targetId: String): UploadQueueEntity?

    @Query(
        """
        SELECT * FROM upload_queue
        WHERE status IN ('UPLOADING', 'RETRYING', 'FAILED', 'AUTH_REQUIRED', 'SYNCED')
        ORDER BY updatedAt DESC
        LIMIT 1
        """
    )
    fun observeLatestVisible(): Flow<UploadQueueEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(queue: UploadQueueEntity)

    @Query(
        """
        UPDATE upload_queue
        SET status = :status,
            attempt = :attempt,
            message = :message,
            lastError = :lastError,
            updatedAt = :updatedAt
        WHERE id = :id
        """
    )
    suspend fun updateStatus(
        id: String,
        status: String,
        attempt: Int,
        message: String?,
        lastError: String?,
        updatedAt: Long
    )

    @Query("DELETE FROM upload_queue WHERE id = :id")
    suspend fun delete(id: String)
}
