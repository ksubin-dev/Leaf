package com.subin.leafy.data.repository

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.Operation
import androidx.work.WorkManager
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.data.datasource.local.LocalNoteDataSource
import com.subin.leafy.data.datasource.local.UploadQueueDataSource
import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.RemoteNoteDataSource
import com.subin.leafy.data.datasource.remote.UserDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.*
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteRepositoryImplTest {

    private val gson = Gson()

    private lateinit var localNoteDataSource: LocalNoteDataSource
    private lateinit var uploadQueueDataSource: UploadQueueDataSource
    private lateinit var remoteNoteDataSource: RemoteNoteDataSource
    private lateinit var authDataSource: AuthDataSource
    private lateinit var userDataSource: UserDataSource
    private lateinit var imageCompressor: ImageCompressor
    private lateinit var workManager: WorkManager

    private lateinit var repository: NoteRepositoryImpl

    @Before
    fun setUp() {
        localNoteDataSource = mockk()
        uploadQueueDataSource = mockk(relaxed = true)
        remoteNoteDataSource = mockk()
        authDataSource = mockk()
        userDataSource = mockk()
        imageCompressor = mockk()
        workManager = mockk()

        repository = NoteRepositoryImpl(
            localNoteDataSource = localNoteDataSource,
            uploadQueueDataSource = uploadQueueDataSource,
            remoteNoteDataSource = remoteNoteDataSource,
            authDataSource = authDataSource,
            userDataSource = userDataSource,
            imageCompressor = imageCompressor,
            workManager = workManager
        )
    }


    private val dummyNote = BrewingNote(
        id = "note_123", ownerId = "user_123", isPublic = true, date = 0L, createdAt = 0L,
        teaInfo = TeaInfo("녹차", "", TeaType.GREEN, "", "", ""),
        recipe = BrewingRecipe(90, 3f, 150, 180, 1, TeawareType.MUG),
        evaluation = SensoryEvaluation(emptyList(), 3, 3, 3, 3, 3, BodyType.MEDIUM, 3, ""),
        rating = RatingInfo(5, true),
        metadata = NoteMetadata(WeatherType.SUNNY, "", emptyList()),
        stats = PostStatistics(0, 0, 0, 0),
        myState = PostSocialState(isLiked = false, isBookmarked = false)
    )


    @Test
    fun `동기화 성공 시 - 원격 데이터를 가져와서 로컬DB를 갱신해야 한다`() = runTest {
        val myUid = "user_123"
        val fakeRemoteNotes = listOf(
            mockk<BrewingNote>(relaxed = true),
            mockk<BrewingNote>(relaxed = true)
        )

        every { authDataSource.getCurrentUserId() } returns myUid
        coEvery { remoteNoteDataSource.getMyBackupNotes(myUid) } returns DataResourceResult.Success(fakeRemoteNotes)
        coEvery { localNoteDataSource.deleteMyAllNotes(myUid) } just Runs
        coEvery { localNoteDataSource.insertNotes(any()) } just Runs

        val result = repository.syncNotes()

        assertThat(result).isInstanceOf(DataResourceResult.Success::class.java)
        coVerify(exactly = 1) { localNoteDataSource.deleteMyAllNotes(myUid) }
        coVerify(exactly = 1) { localNoteDataSource.insertNotes(fakeRemoteNotes) }
    }

    @Test
    fun `동기화 성공 시 - 업로드 큐에 남은 노트는 로컬DB 교체 후에도 보존해야 한다`() = runTest {
        val myUid = "user_123"
        val remoteNote = dummyNote.copy(id = "remote_note", ownerId = myUid)
        val queuedNote = dummyNote.copy(id = "queued_note", ownerId = myUid)
        val queuedUpload = uploadQueue(
            note = queuedNote,
            status = UploadStatus.FAILED,
            imageUriStrings = listOf("file://queued.jpg"),
            isEditMode = false
        )

        every { authDataSource.getCurrentUserId() } returns myUid
        coEvery { remoteNoteDataSource.getMyBackupNotes(myUid) } returns DataResourceResult.Success(listOf(remoteNote))
        coEvery {
            uploadQueueDataSource.getByTargetTypeAndStatuses(
                UploadTargetType.NOTE,
                listOf(
                    UploadStatus.PENDING,
                    UploadStatus.UPLOADING,
                    UploadStatus.RETRYING,
                    UploadStatus.FAILED,
                    UploadStatus.AUTH_REQUIRED
                )
            )
        } returns listOf(queuedUpload)
        coEvery { localNoteDataSource.deleteMyAllNotes(myUid) } just Runs
        coEvery { localNoteDataSource.insertNotes(any()) } just Runs

        val result = repository.syncNotes()

        assertThat(result).isInstanceOf(DataResourceResult.Success::class.java)
        coVerify(exactly = 1) { localNoteDataSource.deleteMyAllNotes(myUid) }
        coVerify(exactly = 1) {
            localNoteDataSource.insertNotes(
                match { notes ->
                    notes.map { it.id } == listOf("remote_note", "queued_note")
                }
            )
        }
    }


    @Test
    fun `동기화 실패 시 (네트워크 오류) - 로컬DB를 절대 지우면 안 된다`() = runTest {
        val myUid = "user_123"
        val networkError = Exception("Network Timeout")

        every { authDataSource.getCurrentUserId() } returns myUid
        coEvery { remoteNoteDataSource.getMyBackupNotes(myUid) } returns DataResourceResult.Failure(networkError)

        val result = repository.syncNotes()

        assertThat(result).isInstanceOf(DataResourceResult.Failure::class.java)
        coVerify(exactly = 0) { localNoteDataSource.deleteMyAllNotes(any()) }
        coVerify(exactly = 0) { localNoteDataSource.insertNotes(any()) }
    }

    @Test
    fun `로그인이 안 된 상태라면 - Firebase 통신 자체를 시도하지 않고 즉시 에러를 반환해야 한다`() = runTest {
        every { authDataSource.getCurrentUserId() } returns null

        val result = repository.syncNotes()

        assertThat(result).isInstanceOf(DataResourceResult.Failure::class.java)
        coVerify(exactly = 0) { remoteNoteDataSource.getMyBackupNotes(any()) }
    }


    @Test
    fun `노트 상세조회 시 - 원격서버 통신에 실패하면 로컬DB에서 데이터를 가져와 성공처리해야 한다`() = runTest {
        val noteId = "note_123"
        val networkError = Exception("Network Timeout")

        every { authDataSource.getCurrentUserId() } returns null
        coEvery { remoteNoteDataSource.getNoteDetail(noteId) } returns DataResourceResult.Failure(networkError)
        coEvery { localNoteDataSource.getNote(noteId) } returns dummyNote

        val result = repository.getNoteDetail(noteId)

        assertThat(result).isInstanceOf(DataResourceResult.Success::class.java)
        coVerify(exactly = 1) { localNoteDataSource.getNote(noteId) }
    }


    @Test
    fun `노트 저장 시 - 로그인된 유저가 없으면 Failure(로그인이 필요합니다)를 반환해야 한다`() = runTest {
        every { authDataSource.getCurrentUserId() } returns null

        val result = repository.saveNote(dummyNote)

        assertThat(result).isInstanceOf(DataResourceResult.Failure::class.java)
        val exception = (result as DataResourceResult.Failure).exception
        assertThat(exception.message).isEqualTo("로그인이 필요합니다.")

        coVerify(exactly = 0) { localNoteDataSource.insertNote(any()) }
        coVerify(exactly = 0) { remoteNoteDataSource.createNote(any()) }
    }

    @Test
    fun `노트 저장 시 - 로컬 이미지 URI가 남아 있으면 원격 저장하지 않고 Failure를 반환해야 한다`() = runTest {
        every { authDataSource.getCurrentUserId() } returns "user_123"
        coEvery { localNoteDataSource.insertNote(any()) } just Runs
        val note = dummyNote.copy(
            metadata = dummyNote.metadata.copy(imageUrls = listOf("file://local-only.jpg"))
        )

        val result = repository.saveNote(note)

        assertThat(result).isInstanceOf(DataResourceResult.Failure::class.java)
        coVerify(exactly = 1) { localNoteDataSource.insertNote(any()) }
        coVerify(exactly = 0) { remoteNoteDataSource.createNote(any()) }
    }

    @Test
    fun `백그라운드 업로드 예약 시 - WorkManager에 올바르게 작업이 큐(Enqueue)에 추가되어야 한다`() = runTest {
        val imageUris = listOf("uri1", "uri2")
        val durableImageUris = listOf("file://internal-1.jpg", "file://internal-2.jpg")
        every {
            workManager.enqueueUniqueWork(
                any(),
                any<ExistingWorkPolicy>(),
                any<OneTimeWorkRequest>()
            )
        } returns mockk<Operation>(relaxed = true)
        coEvery {
            imageCompressor.saveImageToInternalStorage("uri1", "notes/${dummyNote.id}", "note_0")
        } returns durableImageUris[0]
        coEvery {
            imageCompressor.saveImageToInternalStorage("uri2", "notes/${dummyNote.id}", "note_1")
        } returns durableImageUris[1]
        coEvery { localNoteDataSource.insertNote(any()) } just Runs

        repository.scheduleNoteUpload(dummyNote, imageUris, isEditMode = false)

        coVerify(exactly = 1) {
            localNoteDataSource.insertNote(
                match { it.id == dummyNote.id && it.metadata.imageUrls == durableImageUris }
            )
        }
        verify(exactly = 1) {
            workManager.enqueueUniqueWork(
                "upload_note_${dummyNote.id}",
                ExistingWorkPolicy.REPLACE,
                any<OneTimeWorkRequest>()
            )
        }
    }

    @Test
    fun `앱 재실행 복구 시 - PENDING 또는 RETRYING 노트 업로드를 KEEP 정책으로 재등록한다`() = runTest {
        val queuedNote = dummyNote.copy(id = "queued_note")
        val queuedUpload = uploadQueue(
            note = queuedNote,
            status = UploadStatus.RETRYING,
            imageUriStrings = listOf("file://queued.jpg"),
            isEditMode = true
        )
        every {
            workManager.enqueueUniqueWork(
                any(),
                any<ExistingWorkPolicy>(),
                any<OneTimeWorkRequest>()
            )
        } returns mockk<Operation>(relaxed = true)
        coEvery {
            uploadQueueDataSource.getByTargetTypeAndStatuses(
                UploadTargetType.NOTE,
                listOf(UploadStatus.PENDING, UploadStatus.RETRYING)
            )
        } returns listOf(queuedUpload)

        val recoveredCount = repository.recoverQueuedNoteUploads()

        assertThat(recoveredCount).isEqualTo(1)
        coVerify(exactly = 1) {
            uploadQueueDataSource.updateStatus(
                id = "NOTE_queued_note",
                status = UploadStatus.PENDING,
                attempt = 0,
                message = "업로드 대기 중입니다.",
                lastError = null
            )
        }
        verify(exactly = 1) {
            workManager.enqueueUniqueWork(
                "upload_note_queued_note",
                ExistingWorkPolicy.KEEP,
                any<OneTimeWorkRequest>()
            )
        }
    }

    @Test
    fun `앱 재실행 복구 시 - payload가 깨진 노트 큐는 FAILED로 남기고 재등록하지 않는다`() = runTest {
        val invalidQueue = UploadQueue(
            id = "NOTE_broken",
            targetType = UploadTargetType.NOTE,
            targetId = "broken",
            status = UploadStatus.PENDING,
            payload = "{"
        )
        coEvery {
            uploadQueueDataSource.getByTargetTypeAndStatuses(
                UploadTargetType.NOTE,
                listOf(UploadStatus.PENDING, UploadStatus.RETRYING)
            )
        } returns listOf(invalidQueue)

        val recoveredCount = repository.recoverQueuedNoteUploads()

        assertThat(recoveredCount).isEqualTo(0)
        coVerify(exactly = 1) {
            uploadQueueDataSource.updateStatus(
                id = "NOTE_broken",
                status = UploadStatus.FAILED,
                attempt = 0,
                message = "업로드 요청 정보가 올바르지 않습니다.",
                lastError = "Invalid note upload payload"
            )
        }
        verify(exactly = 0) {
            workManager.enqueueUniqueWork(any(), any<ExistingWorkPolicy>(), any<OneTimeWorkRequest>())
        }
    }

    @Test
    fun `앱 재실행 복구 시 - FAILED와 AUTH_REQUIRED 노트 큐는 자동 재등록 대상에서 제외한다`() = runTest {
        coEvery {
            uploadQueueDataSource.getByTargetTypeAndStatuses(
                UploadTargetType.NOTE,
                listOf(UploadStatus.PENDING, UploadStatus.RETRYING)
            )
        } returns emptyList()

        val recoveredCount = repository.recoverQueuedNoteUploads()

        assertThat(recoveredCount).isEqualTo(0)
        coVerify(exactly = 1) {
            uploadQueueDataSource.getByTargetTypeAndStatuses(
                UploadTargetType.NOTE,
                listOf(UploadStatus.PENDING, UploadStatus.RETRYING)
            )
        }
        verify(exactly = 0) {
            workManager.enqueueUniqueWork(any(), any<ExistingWorkPolicy>(), any<OneTimeWorkRequest>())
        }
    }

    private fun uploadQueue(
        note: BrewingNote,
        status: UploadStatus,
        imageUriStrings: List<String>,
        isEditMode: Boolean
    ): UploadQueue {
        val payload = mapOf(
            "note" to note,
            "imageUriStrings" to imageUriStrings,
            "isEditMode" to isEditMode
        )

        return UploadQueue(
            id = "NOTE_${note.id}",
            targetType = UploadTargetType.NOTE,
            targetId = note.id,
            status = status,
            payload = gson.toJson(payload)
        )
    }
}
