package com.subin.leafy.data.datasource.remote.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.subin.leafy.data.datasource.remote.NotificationDataSource
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.COLLECTION_NOTIFICATIONS
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.COLLECTION_USERS
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.FIELD_CREATED_AT
import com.subin.leafy.data.datasource.remote.firestore.FirestoreConstants.FIELD_IS_READ
import com.subin.leafy.data.mapper.toNotificationDomainList
import com.subin.leafy.data.model.dto.NotificationDto
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Notification
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreNotificationDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotificationDataSource {

    override fun getNotifications(userId: String): Flow<DataResourceResult<List<Notification>>> = callbackFlow {
        val query = firestore.collection(COLLECTION_USERS)
            .document(userId)
            .collection(COLLECTION_NOTIFICATIONS)
            .orderBy(FIELD_CREATED_AT, Query.Direction.DESCENDING)
            .limit(50)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(DataResourceResult.Failure(error))
                return@addSnapshotListener
            }
            val notifications = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject<NotificationDto>()?.copy(id = doc.id)
            }?.toNotificationDomainList() ?: emptyList()

            trySend(DataResourceResult.Success(notifications))
        }
        awaitClose { listener.remove() }
    }

    override suspend fun markAsRead(userId: String, notificationId: String) {
        runCatching {
            firestore.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_NOTIFICATIONS)
                .document(notificationId)
                .update(FIELD_IS_READ, true)
                .await()
        }
    }

    override suspend fun deleteNotification(userId: String, notificationId: String) {
        runCatching {
            firestore.collection(COLLECTION_USERS)
                .document(userId)
                .collection(COLLECTION_NOTIFICATIONS)
                .document(notificationId)
                .delete()
                .await()
        }
    }
}