package com.subin.leafy.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.leafy.shared.R
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.UserUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class CommunityUploadWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userUseCases: UserUseCases,
    private val uploadProcessor: CommunityUploadProcessor
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo("게시글을 업로드하고 있어요")
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val userIdResult = userUseCases.getCurrentUserId()
            if (userIdResult !is DataResourceResult.Success) {
                return@withContext Result.failure()
            }

            val request = CommunityUploadRequest.from(inputData, userIdResult.data)
                ?: return@withContext Result.failure()

            setForeground(createForegroundInfo("게시글 등록 중..."))

            return@withContext resultFor(uploadProcessor.upload(request))

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext resultForException(e)
        }
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val channelId = "post_upload_channel"
        val title = "Leafy 커뮤니티"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "게시글 업로드",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setContentTitle(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_leaf)
            .setOngoing(true)
            .setProgress(0, 0, true)
            .build()

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        const val KEY_TITLE = "title"
        const val KEY_CONTENT = "content"
        const val KEY_TAGS = "tags"
        const val KEY_IMAGE_URIS = "image_uris"
        const val KEY_POST_ID = "post_id"
        const val KEY_LINKED_NOTE_ID = "linked_note_id"
        const val KEY_LINKED_TEA_TYPE = "linked_tea_type"
        const val KEY_LINKED_RATING = "linked_rating"
    }
}
