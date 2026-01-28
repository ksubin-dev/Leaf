package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.remote.StorageDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.ImageRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val storageDataSource: StorageDataSource
) : ImageRepository {

    override suspend fun uploadImage(uri: String, folder: String): DataResourceResult<String> {
        return storageDataSource.uploadImage(uri, folder)
    }

    override suspend fun uploadImages(uris: List<String>, folder: String): DataResourceResult<List<String>> {
        if (uris.isEmpty()) return DataResourceResult.Success(emptyList())

        return try {
            coroutineScope {
                val deferredUploads = uris.map { uri ->
                    async { storageDataSource.uploadImage(uri, folder) }
                }

                val results = deferredUploads.awaitAll()

                val successUrls = mutableListOf<String>()
                var hasError = false
                var firstException: Throwable? = null

                for (result in results) {
                    when (result) {
                        is DataResourceResult.Success -> {
                            successUrls.add(result.data)
                        }
                        is DataResourceResult.Failure -> {
                            hasError = true
                            if (firstException == null) {
                                firstException = result.exception
                            }
                        }
                        else -> {
                            hasError = true
                            if (firstException == null) {
                                firstException = Exception("Unknown State returned during upload")
                            }
                        }
                    }
                }
                if (hasError) {
                    successUrls.forEach { url ->
                        async { storageDataSource.deleteImage(url) }
                    }
                    DataResourceResult.Failure(firstException ?: Exception("Image upload failed"))
                } else {
                    DataResourceResult.Success(successUrls)
                }
            }
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun deleteImage(imageUrl: String): DataResourceResult<Unit> {
        return storageDataSource.deleteImage(imageUrl)
    }
}