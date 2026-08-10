package com.subin.leafy.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "upload_queue",
    indices = [Index(value = ["targetType", "targetId"], unique = true)]
)
data class UploadQueueEntity(
    @PrimaryKey val id: String,
    val targetType: String,
    val targetId: String,
    val status: String,
    val attempt: Int,
    val maxAttempts: Int,
    val payload: String?,
    val message: String?,
    val lastError: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val lastNotifiedAt: Long?
)
