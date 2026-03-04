package com.subin.leafy.data.repository

import androidx.work.WorkManager
import com.google.common.truth.Truth.assertThat
import com.subin.leafy.data.datasource.local.LocalNoteDataSource
import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.RemoteNoteDataSource
import com.subin.leafy.data.datasource.remote.UserDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
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
}