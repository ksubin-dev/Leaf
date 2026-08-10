package com.subin.leafy.data.repository

import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.gson.Gson
import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.data.datasource.local.LocalNoteDataSource
import com.subin.leafy.data.datasource.local.UploadQueueDataSource
import com.subin.leafy.data.worker.UploadWorker
import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.RemoteNoteDataSource
import com.subin.leafy.data.datasource.remote.UserDataSource
import com.subin.leafy.data.util.BadgeLibrary
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.PostSocialState
import com.subin.leafy.domain.model.UploadQueue
import com.subin.leafy.domain.model.UploadStatus
import com.subin.leafy.domain.model.UploadTargetType
import com.subin.leafy.domain.model.UserBadge
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val localNoteDataSource: LocalNoteDataSource,
    private val uploadQueueDataSource: UploadQueueDataSource,
    private val remoteNoteDataSource: RemoteNoteDataSource,
    private val authDataSource: AuthDataSource,
    private val userDataSource: UserDataSource,
    private val imageCompressor: ImageCompressor,
    private val workManager: WorkManager
) : NoteRepository {

    private val gson = Gson()

    override fun getMyNotesFlow(): Flow<List<BrewingNote>> {
        val myUid = authDataSource.getCurrentUserId() ?: return flowOf(emptyList())
        return localNoteDataSource.getAllNotesFlow(myUid)
    }

    override fun getNotesByMonthFlow(userId: String, year: Int, month: Int): Flow<List<BrewingNote>> {
        val myUid = authDataSource.getCurrentUserId() ?: return flowOf(emptyList())
        return localNoteDataSource.getNotesByMonthFlow(myUid, year, month)
    }

    override fun searchMyNotes(query: String): Flow<List<BrewingNote>> {
        val myUid = authDataSource.getCurrentUserId() ?: return flowOf(emptyList())
        return localNoteDataSource.searchNotes(myUid, query)
    }

    override suspend fun getNoteDetail(noteId: String): DataResourceResult<BrewingNote> {
        val remoteResult = remoteNoteDataSource.getNoteDetail(noteId)

        var note: BrewingNote? = null

        if (remoteResult is DataResourceResult.Success) {
            note = remoteResult.data

            try {
                localNoteDataSource.insertNote(note)
            } catch (e: Exception) {
            }
        } else {
            note = localNoteDataSource.getNote(noteId)
        }

        if (note == null) {
            return DataResourceResult.Failure(Exception("Note not found"))
        }

        val myUid = authDataSource.getCurrentUserId()
        if (myUid != null) {
            val userResult = userDataSource.getUser(myUid)
            if (userResult is DataResourceResult.Success) {
                val me = userResult.data
                val isLiked = me.likedPostIds.contains(note.id)
                val isBookmarked = me.bookmarkedPostIds.contains(note.id)
                note = note.copy(
                    myState = PostSocialState(isLiked, isBookmarked)
                )
            }
        }

        return DataResourceResult.Success(note)
    }

    override suspend fun saveNote(note: BrewingNote): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val noteToSave = note.copy(ownerId = myUid)

        return try {
            localNoteDataSource.insertNote(noteToSave)
            if (noteToSave.hasLocalImageUris()) {
                return DataResourceResult.Failure(Exception("원격 저장 전 이미지 업로드가 필요합니다."))
            }
            remoteNoteDataSource.createNote(noteToSave)
            checkAndGrantBadges(myUid)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun updateNote(note: BrewingNote): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId() ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val noteToUpdate = note.copy(ownerId = myUid)

        return try {
            localNoteDataSource.updateNote(noteToUpdate)
            if (noteToUpdate.hasLocalImageUris()) {
                return DataResourceResult.Failure(Exception("원격 저장 전 이미지 업로드가 필요합니다."))
            }
            remoteNoteDataSource.updateNote(noteToUpdate)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun deleteNote(noteId: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId() ?: return DataResourceResult.Failure(Exception("로그인 필요"))

        return try {
            localNoteDataSource.deleteNote(noteId)
            remoteNoteDataSource.deleteNote(noteId, myUid)
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun getUserNotes(userId: String): DataResourceResult<List<BrewingNote>> {
        return remoteNoteDataSource.getUserPublicNotes(userId)
    }

    override suspend fun syncNotes(): DataResourceResult<Unit> {
        Log.d("SYNC_LOG", "동기화 시작...")

        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인 필요"))

        val result = remoteNoteDataSource.getMyBackupNotes(myUid)

        return if (result is DataResourceResult.Success) {
            val remoteNotes = result.data

            try {
                val queuedNotes = getQueuedNotePayloads(NOTE_SYNC_PROTECTED_STATUSES)
                val queuedNoteIds = queuedNotes.map { it.id }.toSet()
                val notesToInsert = remoteNotes
                    .filterNot { queuedNoteIds.contains(it.id) } + queuedNotes

                localNoteDataSource.deleteMyAllNotes(myUid)

                if (notesToInsert.isNotEmpty()) {
                    localNoteDataSource.insertNotes(notesToInsert)
                }
                Log.d("SYNC_LOG", "동기화 완료: ${remoteNotes.size}개 로드됨, 보호된 로컬 노트 ${queuedNotes.size}개 유지")
                DataResourceResult.Success(Unit)
            } catch (e: Exception) {
                DataResourceResult.Failure(e)
            }
        } else {
            val exception = (result as DataResourceResult.Failure).exception
            DataResourceResult.Failure(exception)
        }
    }

    override suspend fun clearLocalCache(): DataResourceResult<Unit> {
        return try {
            localNoteDataSource.clearAllTables()
            DataResourceResult.Success(Unit)
        } catch (e: Exception) {
            DataResourceResult.Failure(e)
        }
    }

    override suspend fun scheduleNoteUpload(note: BrewingNote, imageUriStrings: List<String>, isEditMode: Boolean) {
        val queueId = uploadQueueId(UploadTargetType.NOTE, note.id)
        val durableImageUriStrings = imageUriStrings.toDurableNoteImageUris(note.id)
        val localPreviewNote = note.copy(
            metadata = note.metadata.copy(imageUrls = durableImageUriStrings)
        )
        val payload = NoteUploadPayload(
            note = localPreviewNote,
            imageUriStrings = durableImageUriStrings,
            isEditMode = isEditMode
        )

        if (isEditMode) {
            localNoteDataSource.updateNote(localPreviewNote)
        } else {
            localNoteDataSource.insertNote(localPreviewNote)
        }

        uploadQueueDataSource.upsert(
            UploadQueue(
                id = queueId,
                targetType = UploadTargetType.NOTE,
                targetId = note.id,
                status = UploadStatus.PENDING,
                payload = gson.toJson(payload),
                message = "백그라운드 업로드 대기 중입니다."
            )
        )

        enqueueNoteUpload(
            queueId = queueId,
            payload = payload,
            existingWorkPolicy = ExistingWorkPolicy.REPLACE
        )
    }

    override suspend fun recoverQueuedNoteUploads(): Int {
        val queues = uploadQueueDataSource.getByTargetTypeAndStatuses(
            targetType = UploadTargetType.NOTE,
            statuses = NOTE_AUTO_RECOVERY_STATUSES
        )

        var recoveredCount = 0
        queues.forEach { queue ->
            val payload = queue.toNoteUploadPayload()
            if (payload == null) {
                uploadQueueDataSource.updateStatus(
                    id = queue.id,
                    status = UploadStatus.FAILED,
                    attempt = queue.attempt,
                    message = "업로드 요청 정보가 올바르지 않습니다.",
                    lastError = "Invalid note upload payload"
                )
                return@forEach
            }

            uploadQueueDataSource.updateStatus(
                id = queue.id,
                status = UploadStatus.PENDING,
                attempt = 0,
                message = "업로드 대기 중입니다.",
                lastError = queue.lastError
            )
            enqueueNoteUpload(
                queueId = queue.id,
                payload = payload,
                existingWorkPolicy = ExistingWorkPolicy.KEEP
            )
            recoveredCount++
        }

        return recoveredCount
    }

    private fun enqueueNoteUpload(
        queueId: String,
        payload: NoteUploadPayload,
        existingWorkPolicy: ExistingWorkPolicy
    ) {
        val noteJson = gson.toJson(payload.note)
        val imagesJson = gson.toJson(payload.imageUriStrings)

        val inputData = Data.Builder()
            .putString(UploadWorker.KEY_UPLOAD_QUEUE_ID, queueId)
            .putString(UploadWorker.KEY_NOTE_DATA, noteJson)
            .putString(UploadWorker.KEY_IMAGE_URIS, imagesJson)
            .putBoolean(UploadWorker.KEY_IS_EDIT_MODE, payload.isEditMode)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag("upload_note_${payload.note.id}")
            .build()

        workManager.enqueueUniqueWork(
            "upload_note_${payload.note.id}",
            existingWorkPolicy,
            uploadWorkRequest
        )
    }

    private fun uploadQueueId(targetType: UploadTargetType, targetId: String): String {
        return "${targetType.name}_$targetId"
    }

    private suspend fun getQueuedNotePayloads(statuses: List<UploadStatus>): List<BrewingNote> {
        return uploadQueueDataSource.getByTargetTypeAndStatuses(
            targetType = UploadTargetType.NOTE,
            statuses = statuses
        ).mapNotNull { queue ->
            queue.toNoteUploadPayload()?.note ?: localNoteDataSource.getNote(queue.targetId)
        }
    }

    private fun UploadQueue.toNoteUploadPayload(): NoteUploadPayload? {
        return payload?.let {
            runCatching { gson.fromJson(it, NoteUploadPayload::class.java) }.getOrNull()
        }
    }

    private suspend fun List<String>.toDurableNoteImageUris(noteId: String): List<String> {
        return mapIndexed { index, uriString ->
            if (uriString.startsWith("http")) {
                uriString
            } else {
                imageCompressor.saveImageToInternalStorage(
                    imageUriString = uriString,
                    folderName = "notes/$noteId",
                    filePrefix = "note_$index"
                )
            }
        }
    }

    private fun BrewingNote.hasLocalImageUris(): Boolean {
        return metadata.imageUrls.any { !it.startsWith("http") }
    }

    private suspend fun checkAndGrantBadges(userId: String) {
        val currentNotes = localNoteDataSource.getAllNotesFlow(userId).first()
        val count = currentNotes.size

        suspend fun grant(libraryBadge: BadgeLibrary) {
            val badge = UserBadge(
                id = libraryBadge.id,
                name = libraryBadge.title,
                description = libraryBadge.description,
                imageUrl = libraryBadge.imageUrl,
                obtainedAt = System.currentTimeMillis()
            )
            userDataSource.saveUserBadge(userId, badge)
        }

        when (count) {
            1 -> grant(BadgeLibrary.FIRST_BREW)
            10 -> grant(BadgeLibrary.BREWING_ENTHUSIAST)
            50 -> grant(BadgeLibrary.TEA_MASTER)
        }

        if (count >= 5) {
            val recentFive = currentNotes.take(5)
            val firstType = recentFive.first().teaInfo.type
            val isAllSameType = recentFive.all { it.teaInfo.type == firstType }

            if (isAllSameType) {
                grant(BadgeLibrary.ONE_LOVE)
            }
        }
    }

    private data class NoteUploadPayload(
        val note: BrewingNote,
        val imageUriStrings: List<String> = emptyList(),
        val isEditMode: Boolean = false
    )

    private companion object {
        val NOTE_AUTO_RECOVERY_STATUSES = listOf(
            UploadStatus.PENDING,
            UploadStatus.RETRYING
        )

        val NOTE_SYNC_PROTECTED_STATUSES = listOf(
            UploadStatus.PENDING,
            UploadStatus.UPLOADING,
            UploadStatus.RETRYING,
            UploadStatus.FAILED,
            UploadStatus.AUTH_REQUIRED
        )
    }
}
