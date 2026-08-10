package com.subin.leafy.domain.model

enum class UploadTargetType {
    NOTE,
    COMMUNITY
}

enum class UploadStatus {
    PENDING,
    UPLOADING,
    RETRYING,
    SYNCED,
    FAILED,
    AUTH_REQUIRED
}

data class UploadQueue(
    val id: String,
    val targetType: UploadTargetType,
    val targetId: String,
    val status: UploadStatus,
    val attempt: Int = 0,
    val maxAttempts: Int = 3,
    val payload: String? = null,
    val message: String? = null,
    val lastError: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastNotifiedAt: Long? = null
)
