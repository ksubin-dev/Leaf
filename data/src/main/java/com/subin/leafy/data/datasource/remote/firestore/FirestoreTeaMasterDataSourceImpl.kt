package com.subin.leafy.data.datasource.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.subin.leafy.data.datasource.remote.TeaMasterDataSource
import com.subin.leafy.data.mapper.toTeaMasterDomain
import com.subin.leafy.data.model.dto.UserDto
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaMaster
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirestoreTeaMasterDataSourceImpl(
    private val firestore: FirebaseFirestore
) : TeaMasterDataSource {

    override fun getRecommendedMasters(limit: Int): Flow<DataResourceResult<List<TeaMaster>>> = callbackFlow {
        val query = firestore.collection(FirestoreConstants.COLLECTION_USERS)
            .orderBy(FirestoreConstants.FIELD_FOLLOWER_COUNT, Query.Direction.DESCENDING)
            .limit(limit.toLong())

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(DataResourceResult.Failure(error))
                return@addSnapshotListener
            }

            val masters = snapshot?.documents?.mapNotNull { doc ->
                val userDto = doc.toObject<UserDto>()?.copy(uid = doc.id)
                userDto?.toTeaMasterDomain()
            } ?: emptyList()

            trySend(DataResourceResult.Success(masters))
        }
        awaitClose { listener.remove() }
    }
}
