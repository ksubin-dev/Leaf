package com.subin.leafy.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.subin.leafy.data.datasource.local.UploadQueueDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.UploadStatus
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
    private val foregroundInfoProvider: CommunityUploadForegroundInfoProvider,
    private val uploadQueueDataSource: UploadQueueDataSource
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return foregroundInfoProvider.create("게시글을 업로드하고 있어요")
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val queueId = inputData.getString(KEY_UPLOAD_QUEUE_ID)
        try {
            val userIdResult = userUseCases.getCurrentUserId()
            if (userIdResult !is DataResourceResult.Success) {
                queueId?.let {
                    updateQueueStatus(
                        queueId = it,
                        status = UploadStatus.AUTH_REQUIRED,
                        message = "로그인이 필요해요.",
                        lastError = "로그인 필요"
                    )
                }
                return@withContext Result.failure()
            }

            val request = CommunityUploadRequest.from(inputData, userIdResult.data)
            if (request == null) {
                queueId?.let {
                    updateQueueStatus(it, UploadStatus.FAILED, message = "업로드 요청 정보가 올바르지 않습니다.")
                }
                return@withContext Result.failure()
            }

            setForeground(foregroundInfoProvider.create("게시글 등록 중..."))
            queueId?.let {
                updateQueueStatus(
                    queueId = it,
                    status = if (runAttemptCount == 0) UploadStatus.UPLOADING else UploadStatus.RETRYING,
                    attempt = runAttemptCount,
                    message = if (runAttemptCount == 0) {
                        "업로드 중입니다."
                    } else {
                        "업로드 재시도 중 ${runAttemptCount.coerceAtMost(MAX_RETRY_COUNT)}/$MAX_RETRY_COUNT"
                    }
                )
            }

            val uploadResult = uploadProcessor.upload(request)
            val workerResult = resultFor(uploadResult)
            when (uploadResult) {
                is DataResourceResult.Success -> queueId?.let {
                    updateQueueStatus(it, UploadStatus.SYNCED, message = "업로드 완료")
                }
                is DataResourceResult.Failure -> queueId?.let {
                    updateRetryOrFailed(it, uploadResult.exception.message)
                }
                DataResourceResult.Loading -> queueId?.let {
                    updateRetryOrFailed(it, "업로드가 아직 완료되지 않았습니다.")
                }
            }
            return@withContext workerResult

        } catch (e: Exception) {
            e.printStackTrace()
            queueId?.let { updateRetryOrFailed(it, e.message) }
            return@withContext resultForException(e)
        }
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        const val KEY_UPLOAD_QUEUE_ID = "upload_queue_id"
        const val KEY_TITLE = "title"
        const val KEY_CONTENT = "content"
        const val KEY_TAGS = "tags"
        const val KEY_IMAGE_URIS = "image_uris"
        const val KEY_POST_ID = "post_id"
        const val KEY_LINKED_NOTE_ID = "linked_note_id"
        const val KEY_LINKED_TEA_TYPE = "linked_tea_type"
        const val KEY_LINKED_RATING = "linked_rating"
    }

    private suspend fun updateRetryOrFailed(queueId: String, message: String?) {
        val isAuthFailure = message?.contains("login", ignoreCase = true) == true ||
            message?.contains("로그인") == true ||
            message?.contains("permission", ignoreCase = true) == true ||
            message?.contains("권한") == true

        when {
            isAuthFailure -> updateQueueStatus(
                queueId = queueId,
                status = UploadStatus.AUTH_REQUIRED,
                message = "로그인이 필요해요.",
                lastError = message
            )
            runAttemptCount >= MAX_RETRY_COUNT -> updateQueueStatus(
                queueId = queueId,
                status = UploadStatus.FAILED,
                attempt = MAX_RETRY_COUNT,
                message = "업로드에 실패했어요.",
                lastError = message
            )
            else -> {
                val nextAttempt = runAttemptCount + 1
                updateQueueStatus(
                    queueId = queueId,
                    status = UploadStatus.RETRYING,
                    attempt = nextAttempt,
                    message = "업로드 재시도 중 $nextAttempt/$MAX_RETRY_COUNT",
                    lastError = message
                )
            }
        }
    }

    private suspend fun updateQueueStatus(
        queueId: String,
        status: UploadStatus,
        attempt: Int = 0,
        message: String? = null,
        lastError: String? = null
    ) {
        uploadQueueDataSource.updateStatus(
            id = queueId,
            status = status,
            attempt = attempt,
            message = message,
            lastError = lastError
        )
    }
}
