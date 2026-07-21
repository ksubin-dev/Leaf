package com.subin.leafy.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.truth.Truth.assertThat
import com.subin.leafy.domain.common.DataResourceResult
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkerRetryPolicyTest {

    @Test
    fun `최대 재시도 전이면 retry를 반환한다`() {
        val worker = testWorker(runAttemptCount = MAX_RETRY_COUNT - 1)

        val result = worker.retryOrFailure()

        assertThat(result).isInstanceOf(ListenableWorker.Result.retry()::class.java)
    }

    @Test
    fun `최대 재시도 횟수에 도달하면 failure를 반환한다`() {
        val worker = testWorker(runAttemptCount = MAX_RETRY_COUNT)

        val result = worker.retryOrFailure()

        assertThat(result).isInstanceOf(ListenableWorker.Result.failure()::class.java)
    }

    @Test
    fun `성공 결과는 success로 변환한다`() {
        val worker = testWorker()

        val result = worker.resultFor(DataResourceResult.Success(Unit))

        assertThat(result).isInstanceOf(ListenableWorker.Result.success()::class.java)
    }

    @Test
    fun `재시도 가능한 실패는 최대 재시도 전까지 retry로 변환한다`() {
        val worker = testWorker(runAttemptCount = 0)

        val result = worker.resultFor(DataResourceResult.Failure(Exception("Network timeout")))

        assertThat(result).isInstanceOf(ListenableWorker.Result.retry()::class.java)
    }

    @Test
    fun `재시도 가능한 실패도 최대 재시도 횟수에 도달하면 failure로 변환한다`() {
        val worker = testWorker(runAttemptCount = MAX_RETRY_COUNT)

        val result = worker.resultFor(DataResourceResult.Failure(Exception("Storage timeout")))

        assertThat(result).isInstanceOf(ListenableWorker.Result.failure()::class.java)
    }

    @Test
    fun `로그인이나 권한 계열 실패는 즉시 failure로 변환한다`() {
        val worker = testWorker(runAttemptCount = 0)

        val loginResult = worker.resultFor(DataResourceResult.Failure(Exception("로그인이 필요합니다.")))
        val permissionResult = worker.resultFor(DataResourceResult.Failure(Exception("permission denied")))

        assertThat(loginResult).isInstanceOf(ListenableWorker.Result.failure()::class.java)
        assertThat(permissionResult).isInstanceOf(ListenableWorker.Result.failure()::class.java)
    }

    @Test
    fun `validation invalid not found 계열 실패는 즉시 failure로 변환한다`() {
        val worker = testWorker(runAttemptCount = 0)

        val validationResult = worker.resultForException(Exception("validation failed"))
        val invalidResult = worker.resultForException(Exception("invalid image uri"))
        val notFoundResult = worker.resultForException(Exception("not found"))

        assertThat(validationResult).isInstanceOf(ListenableWorker.Result.failure()::class.java)
        assertThat(invalidResult).isInstanceOf(ListenableWorker.Result.failure()::class.java)
        assertThat(notFoundResult).isInstanceOf(ListenableWorker.Result.failure()::class.java)
    }

    @Test
    fun `Loading 상태는 retry 정책을 따른다`() {
        val retryWorker = testWorker(runAttemptCount = 0)
        val failureWorker = testWorker(runAttemptCount = MAX_RETRY_COUNT)

        val retryResult = retryWorker.resultFor(DataResourceResult.Loading)
        val failureResult = failureWorker.resultFor(DataResourceResult.Loading)

        assertThat(retryResult).isInstanceOf(ListenableWorker.Result.retry()::class.java)
        assertThat(failureResult).isInstanceOf(ListenableWorker.Result.failure()::class.java)
    }

    private fun testWorker(runAttemptCount: Int = 0): TestCoroutineWorker {
        val context = mockk<Context>(relaxed = true)
        val workerParameters = mockk<WorkerParameters>(relaxed = true)

        every { workerParameters.inputData } returns Data.EMPTY
        every { workerParameters.runAttemptCount } returns runAttemptCount
        every { workerParameters.tags } returns emptySet()

        return TestCoroutineWorker(context, workerParameters)
    }

    private class TestCoroutineWorker(
        appContext: Context,
        workerParameters: WorkerParameters
    ) : CoroutineWorker(appContext, workerParameters) {
        override suspend fun doWork(): Result = Result.success()
    }
}
