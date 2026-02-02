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
import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.UUID

@HiltWorker
class CommunityUploadWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val postUseCases: PostUseCases,
    private val imageUseCases: ImageUseCases,
    private val userUseCases: UserUseCases,
    private val imageCompressor: ImageCompressor
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return createForegroundInfo("게시글을 업로드하고 있어요")
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        setForeground(createForegroundInfo("이미지 처리 중..."))

        try {
            val title = inputData.getString(KEY_TITLE) ?: ""
            val content = inputData.getString(KEY_CONTENT) ?: ""
            val tags = inputData.getStringArray(KEY_TAGS)?.toList() ?: emptyList()
            val imageUriStrings = inputData.getStringArray(KEY_IMAGE_URIS)?.toList() ?: emptyList()

            val linkedNoteId = inputData.getString(KEY_LINKED_NOTE_ID)
            val linkedTeaType = inputData.getString(KEY_LINKED_TEA_TYPE)
            val linkedRating = inputData.getInt(KEY_LINKED_RATING, -1).takeIf { it != -1 }

            val userIdResult = userUseCases.getCurrentUserId()
            if (userIdResult !is DataResourceResult.Success) {
                return@withContext Result.failure()
            }
            val userId = userIdResult.data
            val imageFolderId = UUID.randomUUID().toString()

            val finalImageUrls = try {
                processImages(userId, imageFolderId, imageUriStrings)
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext Result.retry()
            }

            setForeground(createForegroundInfo("게시글 등록 중..."))

            val result = if (linkedNoteId != null) {
                postUseCases.shareNoteAsPost(
                    noteId = linkedNoteId,
                    content = content,
                    tags = tags,
                    imageUrls = finalImageUrls
                )
            } else {
                postUseCases.createPost(
                    postId = UUID.randomUUID().toString(),
                    title = title,
                    content = content,
                    imageUrls = finalImageUrls,
                    teaType = linkedTeaType,
                    rating = linkedRating,
                    tags = tags,
                    brewingSummary = null,
                    originNoteId = null
                )
            }

            return@withContext when (result) {
                is DataResourceResult.Success -> Result.success()
                is DataResourceResult.Failure -> Result.retry()
                else -> Result.failure()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Result.failure()
        }
    }

    private suspend fun processImages(
        userId: String,
        folderId: String,
        uriStrings: List<String>
    ): List<String> = coroutineScope {
        uriStrings.map { uriString ->
            async {
                if (uriString.startsWith("http")) {
                    uriString
                } else {
                    val compressedUri = withContext(Dispatchers.Default) {
                        imageCompressor.compressImage(uriString)
                    }
                    val uploadPath = "posts/$userId/$folderId"

                    val uploadResult = imageUseCases.uploadImage(compressedUri, uploadPath)

                    if (uploadResult is DataResourceResult.Success) {
                        uploadResult.data
                    } else {
                        throw Exception("Image upload failed")
                    }
                }
            }
        }.awaitAll()
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
        const val KEY_LINKED_NOTE_ID = "linked_note_id"
        const val KEY_LINKED_TEA_TYPE = "linked_tea_type"
        const val KEY_LINKED_RATING = "linked_rating"
    }
}