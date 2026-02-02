package com.subin.leafy.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import com.leafy.shared.utils.ImageCompressor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class ProfileUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val userUseCases: UserUseCases,
    private val imageUseCases: ImageUseCases,
    private val imageCompressor: ImageCompressor
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val userId = inputData.getString(KEY_USER_ID)
            val nickname = inputData.getString(KEY_NICKNAME)
            val bio = inputData.getString(KEY_BIO) ?: ""
            val imageUriString = inputData.getString(KEY_IMAGE_URI)

            if (userId == null || nickname == null) {
                return@withContext Result.failure()
            }

            val finalImageUrl = if (imageUriString != null && !imageUriString.startsWith("http")) {
                val compressedPath = imageCompressor.compressImage(imageUriString)
                val uploadPath = "profile_images/$userId"
                val result = imageUseCases.uploadImage(compressedPath, uploadPath)

                if (result is DataResourceResult.Success) {
                    result.data
                } else {
                    throw Exception("이미지 업로드 실패")
                }
            } else {
                imageUriString
            }

            val updateResult = userUseCases.updateProfile(userId, nickname, bio, finalImageUrl)

            if (updateResult is DataResourceResult.Success) {
                Result.success()
            } else {
                Result.retry()
            }

        } catch (e: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val KEY_USER_ID = "key_user_id"
        const val KEY_NICKNAME = "key_nickname"
        const val KEY_BIO = "key_bio"
        const val KEY_IMAGE_URI = "key_image_uri"
    }
}