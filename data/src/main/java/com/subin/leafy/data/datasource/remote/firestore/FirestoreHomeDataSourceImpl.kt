package com.subin.leafy.data.datasource.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.subin.leafy.data.datasource.remote.HomeDataSource
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.COLLECTION_HOME_CONTENTS
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.DOCUMENT_DAILY
import com.subin.leafy.data.mapper.toDomain
import com.subin.leafy.data.model.dto.HomeContentDto
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.HomeContent
import kotlinx.coroutines.tasks.await

class FirestoreHomeDataSourceImpl(
    private val firestore: FirebaseFirestore
) : HomeDataSource {

    override suspend fun getHomeContent(): DataResourceResult<HomeContent> {
        return try {
            val snapshot = firestore.collection(COLLECTION_HOME_CONTENTS)
                .document(DOCUMENT_DAILY)
                .get()
                .await()
            val dto = snapshot.toObject<HomeContentDto>()

            if (dto != null) {
                DataResourceResult.Success(dto.toDomain())
            } else {
                DataResourceResult.Failure(Exception("오늘의 홈 컨텐츠가 없습니다."))
            }
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }
}