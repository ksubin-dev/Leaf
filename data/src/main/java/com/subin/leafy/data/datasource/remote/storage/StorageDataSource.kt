package com.subin.leafy.data.datasource.remote.storage

import com.subin.leafy.domain.common.DataResourceResult

interface StorageDataSource {
    suspend fun uploadImage(uriString: String, folderPath: String): DataResourceResult<String>
    suspend fun deleteImage(imageUrl: String): DataResourceResult<Unit>
}