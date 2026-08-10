package com.subin.leafy.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.data.datasource.local.UploadQueueDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.UploadStatus
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val noteUseCases: NoteUseCases,
    private val imageUseCases: ImageUseCases,
    private val imageCompressor: ImageCompressor,
    private val uploadQueueDataSource: UploadQueueDataSource
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val queueId = inputData.getString(KEY_UPLOAD_QUEUE_ID)
        try {
            val noteJson = inputData.getString(KEY_NOTE_DATA)
            val imageUrisJson = inputData.getString(KEY_IMAGE_URIS)
            val isEditMode = inputData.getBoolean(KEY_IS_EDIT_MODE, false)

            if (noteJson == null || imageUrisJson == null) {
                queueId?.let {
                    updateQueueStatus(it, UploadStatus.FAILED, message = "업로드 요청 정보가 올바르지 않습니다.")
                }
                return@withContext Result.failure()
            }

            val gson = Gson()
            val noteData = gson.fromJson(noteJson, BrewingNote::class.java)
            val typeToken = object : TypeToken<List<String>>() {}.type
            val imageUriStrings: List<String> = gson.fromJson(imageUrisJson, typeToken)
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

            val finalImageUrls = try {
                imageUriStrings.mapIndexed { index, uriString ->
                    uploadNoteImageIfNeeded(uriString, noteData, index)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                queueId?.let { updateRetryOrFailed(it, e.message) }
                return@withContext resultForException(e)
            }

            val updatedMetadata = noteData.metadata.copy(imageUrls = finalImageUrls)
            val finalNote = noteData.copy(metadata = updatedMetadata)

            val saveResult = if (isEditMode) {
                noteUseCases.updateNote(finalNote)
            } else {
                noteUseCases.saveNote(finalNote)
            }

            val result = resultFor(saveResult)
            when (saveResult) {
                is DataResourceResult.Success -> queueId?.let {
                    updateQueueStatus(it, UploadStatus.SYNCED, message = "업로드 완료")
                }
                is DataResourceResult.Failure -> queueId?.let {
                    updateRetryOrFailed(it, saveResult.exception.message)
                }
                DataResourceResult.Loading -> queueId?.let {
                    updateRetryOrFailed(it, "업로드가 아직 완료되지 않았습니다.")
                }
            }
            result

        } catch (e: Exception) {
            e.printStackTrace()
            queueId?.let { updateRetryOrFailed(it, e.message) }
            resultForException(e)
        }
    }

    private suspend fun uploadNoteImageIfNeeded(
        uriString: String,
        note: BrewingNote,
        index: Int
    ): String {
        if (uriString.startsWith("http")) return uriString

        val compressedPath = imageCompressor.compressImage(uriString)
        val uploadPath = "notes/${note.ownerId}/${note.id}/image_$index.jpg"

        return when (val result = imageUseCases.uploadImage(compressedPath, uploadPath)) {
            is DataResourceResult.Success -> result.data
            is DataResourceResult.Failure -> throw result.exception
            else -> throw IllegalStateException("Image upload did not complete")
        }
    }

    companion object {
        const val KEY_UPLOAD_QUEUE_ID = "key_upload_queue_id"
        const val KEY_NOTE_DATA = "key_note_data"
        const val KEY_IMAGE_URIS = "key_image_uris"
        const val KEY_IS_EDIT_MODE = "key_is_edit_mode"
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
