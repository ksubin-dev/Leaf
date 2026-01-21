package com.subin.leafy.data.datasource.remote.storage

import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import com.subin.leafy.data.datasource.remote.StorageDataSource
import com.subin.leafy.domain.common.DataResourceResult
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseStorageDataSourceImpl(
    private val firebaseStorage: FirebaseStorage
) : StorageDataSource {
    override suspend fun uploadImage(uriString: String, folderPath: String): DataResourceResult<String> {
        return try {
            val uri = uriString.toUri()
            val fileName = UUID.randomUUID().toString()
            val storageRef = firebaseStorage.reference
                .child(folderPath)
                .child(fileName)
            storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            DataResourceResult.Success(downloadUrl)

        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun deleteImage(imageUrl: String): DataResourceResult<Unit> {
        return try {
            val storageRef = firebaseStorage.getReferenceFromUrl(imageUrl)
            storageRef.delete().await()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }
}