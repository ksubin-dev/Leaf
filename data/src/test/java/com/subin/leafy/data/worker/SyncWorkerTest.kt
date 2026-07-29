package com.subin.leafy.data.worker

import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.truth.Truth.assertThat
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.TeaUseCases
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SyncWorkerTest {

    @Test
    fun `SyncWorker는 Note와 Tea 동기화가 모두 성공하면 success를 반환한다`() = runTest {
        val worker = syncWorker(
            noteResult = DataResourceResult.Success(Unit),
            teaResult = DataResourceResult.Success(Unit)
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.success()::class.java)
    }

    @Test
    fun `SyncWorker는 Note 동기화 실패가 재시도 가능하면 retry를 반환한다`() = runTest {
        val worker = syncWorker(
            noteResult = retryableFailure(),
            teaResult = DataResourceResult.Success(Unit),
            runAttemptCount = 0
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.retry()::class.java)
    }

    @Test
    fun `SyncWorker는 Tea 동기화 실패가 재시도 가능하면 retry를 반환한다`() = runTest {
        val worker = syncWorker(
            noteResult = DataResourceResult.Success(Unit),
            teaResult = retryableFailure(),
            runAttemptCount = 0
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.retry()::class.java)
    }

    @Test
    fun `SyncWorker는 Loading 결과가 있으면 retry를 반환한다`() = runTest {
        val worker = syncWorker(
            noteResult = DataResourceResult.Loading,
            teaResult = DataResourceResult.Success(Unit),
            runAttemptCount = 0
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.retry()::class.java)
    }

    @Test
    fun `SyncWorker는 재시도 가능한 실패가 최대 재시도에 도달하면 failure를 반환한다`() = runTest {
        val worker = syncWorker(
            noteResult = retryableFailure(),
            teaResult = DataResourceResult.Success(Unit),
            runAttemptCount = MAX_RETRY_COUNT
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.failure()::class.java)
    }

    @Test
    fun `SyncWorker는 non retryable 실패를 즉시 failure로 반환한다`() = runTest {
        val worker = syncWorker(
            noteResult = DataResourceResult.Failure(Exception("로그인이 필요합니다.")),
            teaResult = DataResourceResult.Success(Unit),
            runAttemptCount = 0
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.failure()::class.java)
    }

    @Test
    fun `SyncWorker는 동기화 예외가 재시도 가능하면 retry를 반환한다`() = runTest {
        val noteUseCases = mockk<NoteUseCases>(relaxed = true)
        val teaUseCases = mockk<TeaUseCases>(relaxed = true)
        coEvery { noteUseCases.syncNotes() } throws Exception("Firestore unavailable")
        coEvery { teaUseCases.syncTeas() } returns DataResourceResult.Success(Unit)
        val worker = syncWorker(
            noteUseCases = noteUseCases,
            teaUseCases = teaUseCases,
            runAttemptCount = 0
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.retry()::class.java)
    }

    private fun syncWorker(
        noteResult: DataResourceResult<Unit>,
        teaResult: DataResourceResult<Unit>,
        runAttemptCount: Int = 0
    ): SyncWorker {
        val noteUseCases = mockk<NoteUseCases>(relaxed = true)
        val teaUseCases = mockk<TeaUseCases>(relaxed = true)
        coEvery { noteUseCases.syncNotes() } returns noteResult
        coEvery { teaUseCases.syncTeas() } returns teaResult

        return syncWorker(
            noteUseCases = noteUseCases,
            teaUseCases = teaUseCases,
            runAttemptCount = runAttemptCount
        )
    }

    private fun syncWorker(
        noteUseCases: NoteUseCases,
        teaUseCases: TeaUseCases,
        runAttemptCount: Int = 0
    ): SyncWorker {
        return SyncWorker(
            appContext = mockk(relaxed = true),
            workerParams = workerParameters(runAttemptCount),
            noteUseCases = noteUseCases,
            teaUseCases = teaUseCases
        )
    }

    private fun workerParameters(runAttemptCount: Int): WorkerParameters {
        val workerParameters = mockk<WorkerParameters>(relaxed = true)
        every { workerParameters.inputData } returns Data.EMPTY
        every { workerParameters.runAttemptCount } returns runAttemptCount
        every { workerParameters.tags } returns emptySet()
        return workerParameters
    }

    private fun retryableFailure(): DataResourceResult.Failure {
        return DataResourceResult.Failure(Exception("Firebase timeout"))
    }
}
