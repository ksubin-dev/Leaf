package com.subin.leafy.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.TeaUseCases
import com.leafy.shared.utils.ImageCompressor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class TeaUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val teaUseCases: TeaUseCases,
    private val imageUseCases: ImageUseCases,
    private val imageCompressor: ImageCompressor
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val teaJson = inputData.getString(KEY_TEA_DATA)
            val imageUriString = inputData.getString(KEY_IMAGE_URI)

            if (teaJson == null) return@withContext Result.failure()

            val gson = Gson()
            val teaData = gson.fromJson(teaJson, TeaItem::class.java)


            val finalImageUrl = if (imageUriString != null && !imageUriString.startsWith("http")) {
                val compressedPath = imageCompressor.compressImage(imageUriString)
                val uploadPath = "teas/${teaData.ownerId}/${teaData.id}"
                val result = imageUseCases.uploadImage(compressedPath, uploadPath)

                if (result is DataResourceResult.Success) {
                    result.data
                } else {
                    throw Exception("이미지 업로드 실패")
                }
            } else {
                imageUriString
            }

            val finalTea = teaData.copy(imageUrl = finalImageUrl)

            val saveResult = teaUseCases.saveTea(finalTea)

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
        const val KEY_TEA_DATA = "key_tea_data"
        const val KEY_IMAGE_URI = "key_image_uri"
    }
}