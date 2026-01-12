package com.subin.leafy.data.datasource.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject // [필수] Kotlin Extension
import com.subin.leafy.data.datasource.remote.TeaMasterDataSource
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.COLLECTION_TEA_MASTERS
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.FIELD_FOLLOWER_COUNT_SIMPLE
import com.subin.leafy.data.mapper.toDomain
import com.subin.leafy.data.model.dto.TeaMasterDto
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaMaster
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirestoreTeaMasterDataSourceImpl(
    private val firestore: FirebaseFirestore
) : TeaMasterDataSource {

    override fun getRecommendedMasters(): Flow<DataResourceResult<List<TeaMaster>>> = callbackFlow {
        // 1. 쿼리 작성: 팔로워 많은 순으로 상위 10명 조회
        val query = firestore.collection(COLLECTION_TEA_MASTERS)
            .orderBy(FIELD_FOLLOWER_COUNT_SIMPLE, Query.Direction.DESCENDING)
            .limit(10)

        // 2. 실시간 리스너 등록
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(DataResourceResult.Failure(error))
                return@addSnapshotListener
            }

            val masters = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject<TeaMasterDto>()?.copy(id = doc.id)
            }?.map { dto ->
                // 4. Domain 변환
                // 주의: DataSource 레벨에서는 내가 팔로우했는지 알 수 없으므로 일단 false로 둡니다.
                // 나중에 Repository에서 내 팔로잉 목록(UserDataSource)과 비교해서 true/false를 갱신합니다.
                dto.toDomain(isFollowing = false)
            } ?: emptyList()

            trySend(DataResourceResult.Success(masters))
        }

        // 5. 리스너 해제 (Flow가 닫힐 때)
        awaitClose { listener.remove() }
    }
}