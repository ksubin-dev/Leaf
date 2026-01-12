package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.remote.StorageDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.ImageRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class ImageRepositoryImpl(
    private val storageDataSource: StorageDataSource
) : ImageRepository {

    override suspend fun uploadImage(uri: String, folder: String): DataResourceResult<String> {
        return storageDataSource.uploadImage(uri, folder)
    }

    //다중 업로드
    override suspend fun uploadImages(uris: List<String>, folder: String): DataResourceResult<List<String>> {
        if (uris.isEmpty()) return DataResourceResult.Success(emptyList())

        return try {
            coroutineScope {
                val deferredUploads = uris.map { uri ->
                    async { storageDataSource.uploadImage(uri, folder) }
                }

                val results = deferredUploads.awaitAll()

                // 2. 결과 분석
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
                // 3. 롤백 결정
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