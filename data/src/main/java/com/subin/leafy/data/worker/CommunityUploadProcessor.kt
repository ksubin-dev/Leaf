package com.subin.leafy.data.worker

import androidx.work.Data
import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import javax.inject.Inject

data class CommunityUploadRequest(
    val userId: String,
    val postId: String,
    val title: String,
    val content: String,
    val tags: List<String>,
    val imageUriStrings: List<String>,
    val linkedNoteId: String?,
    val linkedTeaType: String?,
    val linkedRating: Int?
) {
    companion object {
        fun from(inputData: Data, userId: String): CommunityUploadRequest? {
            val postId = inputData.getString(CommunityUploadWorker.KEY_POST_ID) ?: return null

            return CommunityUploadRequest(
                userId = userId,
                postId = postId,
                title = inputData.getString(CommunityUploadWorker.KEY_TITLE) ?: "",
                content = inputData.getString(CommunityUploadWorker.KEY_CONTENT) ?: "",
                tags = inputData.getStringArray(CommunityUploadWorker.KEY_TAGS)?.toList() ?: emptyList(),
                imageUriStrings = inputData.getStringArray(CommunityUploadWorker.KEY_IMAGE_URIS)?.toList()
                    ?: emptyList(),
                linkedNoteId = inputData.getString(CommunityUploadWorker.KEY_LINKED_NOTE_ID),
                linkedTeaType = inputData.getString(CommunityUploadWorker.KEY_LINKED_TEA_TYPE),
                linkedRating = inputData.getInt(CommunityUploadWorker.KEY_LINKED_RATING, -1)
                    .takeIf { it != -1 }
            )
        }
    }
}

class CommunityUploadProcessor @Inject constructor(
    private val postUseCases: PostUseCases,
    private val imageUseCases: ImageUseCases,
    private val imageCompressor: ImageCompressor
) {
    suspend fun upload(request: CommunityUploadRequest): DataResourceResult<Unit> {
        val imageUrls = when (val result = processImages(request)) {
            is DataResourceResult.Success -> result.data
            is DataResourceResult.Failure -> return result
            DataResourceResult.Loading -> return DataResourceResult.Loading
        }

        return if (request.linkedNoteId != null) {
            postUseCases.shareNoteAsPost(
                noteId = request.linkedNoteId,
                content = request.content,
                tags = request.tags,
                imageUrls = imageUrls,
                postId = request.postId
            )
        } else {
            postUseCases.createPost(
                postId = request.postId,
                title = request.title,
                content = request.content,
                imageUrls = imageUrls,
                teaType = request.linkedTeaType,
                rating = request.linkedRating,
                tags = request.tags,
                brewingSummary = null,
                originNoteId = null
            )
        }
    }

    private suspend fun processImages(request: CommunityUploadRequest): DataResourceResult<List<String>> {
        return try {
            val imageUrls = request.imageUriStrings.mapIndexed { index, uriString ->
                uploadImageIfNeeded(
                    userId = request.userId,
                    postId = request.postId,
                    index = index,
                    uriString = uriString
                )
            }

            DataResourceResult.Success(imageUrls)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    private suspend fun uploadImageIfNeeded(
        userId: String,
        postId: String,
        index: Int,
        uriString: String
    ): String {
        if (uriString.startsWith("http")) return uriString

        val compressedPath = imageCompressor.compressImage(uriString)
        val uploadPath = "posts/$userId/$postId/image_$index.jpg"

        return when (val result = imageUseCases.uploadImage(compressedPath, uploadPath)) {
            is DataResourceResult.Success -> result.data
            is DataResourceResult.Failure -> throw result.exception
            DataResourceResult.Loading -> throw IllegalStateException("Image upload did not complete")
        }
    }
}
