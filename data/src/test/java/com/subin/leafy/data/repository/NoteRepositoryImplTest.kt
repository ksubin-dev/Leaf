package com.subin.leafy.data.repository

import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.common.truth.Truth.assertThat
import com.subin.leafy.data.datasource.local.LocalNoteDataSource
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

    private lateinit var localNoteDataSource: LocalNoteDataSource
    private lateinit var remoteNoteDataSource: RemoteNoteDataSource
    private lateinit var authDataSource: AuthDataSource
    private lateinit var userDataSource: UserDataSource
    private lateinit var workManager: WorkManager

    private lateinit var repository: NoteRepositoryImpl

    @Before
    fun setUp() {
        localNoteDataSource = mockk()
        remoteNoteDataSource = mockk()
        authDataSource = mockk()
        userDataSource = mockk()
        workManager = mockk()

        repository = NoteRepositoryImpl(
            localNoteDataSource = localNoteDataSource,
            remoteNoteDataSource = remoteNoteDataSource,
            authDataSource = authDataSource,
            userDataSource = userDataSource,
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
    fun `백그라운드 업로드 예약 시 - WorkManager에 올바르게 작업이 큐(Enqueue)에 추가되어야 한다`() = runTest {
        val imageUris = listOf("uri1", "uri2")
        every { workManager.enqueue(any<WorkRequest>()) } returns mockk(relaxed = true)

        repository.scheduleNoteUpload(dummyNote, imageUris, isEditMode = false)

        verify(exactly = 1) { workManager.enqueue(any<WorkRequest>()) }
    }
}