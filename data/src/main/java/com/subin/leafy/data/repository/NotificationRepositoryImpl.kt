package com.subin.leafy.data.repository

import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.NotificationDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Notification
import com.subin.leafy.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDataSource: NotificationDataSource,
    private val authDataSource: AuthDataSource
) : NotificationRepository {

    override fun getNotificationsFlow(): Flow<DataResourceResult<List<Notification>>> {
        val myUid = authDataSource.getCurrentUserId()

        return if (myUid != null) {
            notificationDataSource.getNotifications(myUid)
        } else {

            flow { emit(DataResourceResult.Failure(Exception("Login required"))) }
        }
    }

    override suspend fun markAsRead(notificationId: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("Login required"))

        return try {
            notificationDataSource.markAsRead(myUid, notificationId)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun deleteNotification(notificationId: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("Login required"))

        return try {
            notificationDataSource.deleteNotification(myUid, notificationId)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }
}