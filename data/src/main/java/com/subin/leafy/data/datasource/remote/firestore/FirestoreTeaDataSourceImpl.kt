package com.subin.leafy.data.datasource.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.toObject
import com.subin.leafy.data.datasource.remote.RemoteTeaDataSource
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.COLLECTION_TEAS
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.FIELD_OWNER_ID
import com.subin.leafy.data.mapper.toDomain
import com.subin.leafy.data.mapper.toDto
import com.subin.leafy.data.model.dto.TeaItemDto
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaItem
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreTeaDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RemoteTeaDataSource {

    private val teasCollection = firestore.collection(COLLECTION_TEAS)

    // 1. 백업 가져오기
    override suspend fun getMyBackupTeas(userId: String): DataResourceResult<List<TeaItem>> {
        return try {
            val snapshot = teasCollection
                .whereEqualTo(FIELD_OWNER_ID, userId)
                .get()
                .await()

            val teas = snapshot.documents.mapNotNull { doc ->
                doc.toObject<TeaItemDto>()?.copy(id = doc.id)?.toDomain()
            }
            DataResourceResult.Success(teas)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    // 2. 저장 (Create & Update)
    override suspend fun saveTea(tea: TeaItem): DataResourceResult<Unit> {
        return try {
            val dto = tea.toDto()

            teasCollection.document(tea.id)
                .set(dto, SetOptions.merge())
                .await()

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    // 3. 삭제
    override suspend fun deleteTea(teaId: String): DataResourceResult<Unit> {
        return try {
            teasCollection.document(teaId)
                .delete()
                .await()

            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }
}