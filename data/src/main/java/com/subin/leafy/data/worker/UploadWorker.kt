package com.subin.leafy.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.leafy.shared.utils.ImageCompressor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val noteUseCases: NoteUseCases,
    private val imageUseCases: ImageUseCases,
    private val imageCompressor: ImageCompressor
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val noteJson = inputData.getString(KEY_NOTE_DATA)
            val imageUrisJson = inputData.getString(KEY_IMAGE_URIS)
            val isEditMode = inputData.getBoolean(KEY_IS_EDIT_MODE, false)

            if (noteJson == null || imageUrisJson == null) {
                return@withContext Result.failure()
            }

            val gson = Gson()
            val noteData = gson.fromJson(noteJson, BrewingNote::class.java)
            val typeToken = object : TypeToken<List<String>>() {}.type
            val imageUriStrings: List<String> = gson.fromJson(imageUrisJson, typeToken)

            val finalImageUrls = imageUriStrings.map { uriString ->
                async {
                    if (uriString.startsWith("http")) {
                        uriString
                    } else {
                        val compressedPath = imageCompressor.compressImage(uriString)
                        val uploadPath = "notes/${noteData.ownerId}/${noteData.id}"
                        val result = imageUseCases.uploadImage(compressedPath, uploadPath)

                        if (result is DataResourceResult.Success) {
                            result.data
                        } else {
                            throw Exception("이미지 업로드 실패")
                        }
                    }
                }
            }.awaitAll()

            val updatedMetadata = noteData.metadata.copy(imageUrls = finalImageUrls)
            val finalNote = noteData.copy(metadata = updatedMetadata)

            val saveResult = if (isEditMode) {
                noteUseCases.updateNote(finalNote)
            } else {
                noteUseCases.saveNote(finalNote)
            }

            if (saveResult is DataResourceResult.Success) {
                Result.success()
            } else {
                Result.retry()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    companion object {
        const val KEY_NOTE_DATA = "key_note_data"
        const val KEY_IMAGE_URIS = "key_image_uris"
        const val KEY_IS_EDIT_MODE = "key_is_edit_mode"
    }
}