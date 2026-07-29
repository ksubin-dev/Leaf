package com.subin.leafy.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
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
    private val uploadProcessor: CommunityUploadProcessor,
    private val foregroundInfoProvider: CommunityUploadForegroundInfoProvider
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return foregroundInfoProvider.create("게시글을 업로드하고 있어요")
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val userIdResult = userUseCases.getCurrentUserId()
            if (userIdResult !is DataResourceResult.Success) {
                return@withContext Result.failure()
            }

            val request = CommunityUploadRequest.from(inputData, userIdResult.data)
                ?: return@withContext Result.failure()

            setForeground(foregroundInfoProvider.create("게시글 등록 중..."))

            return@withContext resultFor(uploadProcessor.upload(request))

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext resultForException(e)
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
