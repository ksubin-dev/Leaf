package com.subin.leafy.data.remote.firestore

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.subin.leafy.data.datasource.StorageDataSource
import com.subin.leafy.domain.common.DataResourceResult
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseStorageDataSourceImpl(
    private val storage: FirebaseStorage
) : StorageDataSource {

    override suspend fun uploadImage(uriString: String, folderPath: String): DataResourceResult<String> = runCatching {
        val uri = Uri.parse(uriString)
        val fileName = "${UUID.randomUUID()}.jpg"
        val fileRef = storage.reference.child("$folderPath/$fileName")
        fileRef.putFile(uri).await()
        val downloadUrl = fileRef.downloadUrl.await()
        DataResourceResult.Success(downloadUrl.toString())
    }.getOrElse {
        DataResourceResult.Failure(it)
    }

    override suspend fun deleteImage(imageUrl: String): DataResourceResult<Unit> = runCatching {
        storage.getReferenceFromUrl(imageUrl).delete().await()
        DataResourceResult.Success(Unit)
    }.getOrElse {
        DataResourceResult.Failure(it)
    }
}