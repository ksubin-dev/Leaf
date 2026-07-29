package com.subin.leafy.data.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.ForegroundUpdater
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.truth.Truth.assertThat
import com.google.common.util.concurrent.Futures
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.UserUseCases
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class CommunityUploadWorkerForegroundTest {

    @Test
    fun `getForegroundInfo는 foreground provider 결과를 반환한다`() = runTest {
        val foregroundInfoProvider = mockk<CommunityUploadForegroundInfoProvider>()
        val foregroundInfo = mockk<ForegroundInfo>()
        every { foregroundInfoProvider.create("게시글을 업로드하고 있어요") } returns foregroundInfo
        val worker = communityUploadWorker(foregroundInfoProvider = foregroundInfoProvider)

        val result = worker.getForegroundInfo()

        assertThat(result).isSameInstanceAs(foregroundInfo)
        verify(exactly = 1) { foregroundInfoProvider.create("게시글을 업로드하고 있어요") }
    }

    @Test
    fun `Community Worker는 foreground 설정 후 processor 성공 결과를 success로 변환한다`() = runTest {
        val userUseCases = mockk<UserUseCases>(relaxed = true)
        val uploadProcessor = mockk<CommunityUploadProcessor>(relaxed = true)
        val foregroundInfoProvider = mockk<CommunityUploadForegroundInfoProvider>()
        val foregroundUpdater = mockk<ForegroundUpdater>()
        val foregroundInfo = mockk<ForegroundInfo>()
        val context = mockk<Context>(relaxed = true)
        val workId = UUID.randomUUID()
        every { foregroundInfoProvider.create("게시글 등록 중...") } returns foregroundInfo
        every {
            foregroundUpdater.setForegroundAsync(context, workId, foregroundInfo)
        } returns Futures.immediateVoidFuture()
        coEvery { userUseCases.getCurrentUserId() } returns DataResourceResult.Success("user-123")
        coEvery { uploadProcessor.upload(any()) } returns DataResourceResult.Success(Unit)
        val worker = communityUploadWorker(
            context = context,
            inputData = communityUploadData(),
            workId = workId,
            foregroundUpdater = foregroundUpdater,
            userUseCases = userUseCases,
            uploadProcessor = uploadProcessor,
            foregroundInfoProvider = foregroundInfoProvider
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.success()::class.java)
        verify(exactly = 1) { foregroundInfoProvider.create("게시글 등록 중...") }
        verify(exactly = 1) { foregroundUpdater.setForegroundAsync(context, workId, foregroundInfo) }
        coVerify(exactly = 1) {
            uploadProcessor.upload(
                CommunityUploadRequest(
                    userId = "user-123",
                    postId = "post-123",
                    title = "첫 커뮤니티 글",
                    content = "차 향이 아주 좋아서 공유해요",
                    tags = listOf("우롱", "향"),
                    imageUriStrings = listOf("file://post.jpg"),
                    linkedNoteId = null,
                    linkedTeaType = "OOLONG",
                    linkedRating = 4
                )
            )
        }
    }

    @Test
    fun `Community Worker는 processor 재시도 가능 실패를 retry로 변환한다`() = runTest {
        val userUseCases = mockk<UserUseCases>(relaxed = true)
        val uploadProcessor = mockk<CommunityUploadProcessor>(relaxed = true)
        val foregroundInfoProvider = mockk<CommunityUploadForegroundInfoProvider>()
        val foregroundUpdater = mockk<ForegroundUpdater>()
        val foregroundInfo = mockk<ForegroundInfo>()
        val context = mockk<Context>(relaxed = true)
        val workId = UUID.randomUUID()
        every { foregroundInfoProvider.create("게시글 등록 중...") } returns foregroundInfo
        every {
            foregroundUpdater.setForegroundAsync(context, workId, foregroundInfo)
        } returns Futures.immediateVoidFuture()
        coEvery { userUseCases.getCurrentUserId() } returns DataResourceResult.Success("user-123")
        coEvery {
            uploadProcessor.upload(any())
        } returns DataResourceResult.Failure(Exception("Firebase Storage timeout"))
        val worker = communityUploadWorker(
            context = context,
            inputData = communityUploadData(),
            workId = workId,
            foregroundUpdater = foregroundUpdater,
            userUseCases = userUseCases,
            uploadProcessor = uploadProcessor,
            foregroundInfoProvider = foregroundInfoProvider,
            runAttemptCount = 0
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.retry()::class.java)
        verify(exactly = 1) { foregroundUpdater.setForegroundAsync(context, workId, foregroundInfo) }
    }

    @Test
    fun `Community Worker는 userId 조회 실패 시 foreground와 processor를 호출하지 않고 failure를 반환한다`() = runTest {
        val userUseCases = mockk<UserUseCases>(relaxed = true)
        val uploadProcessor = mockk<CommunityUploadProcessor>(relaxed = true)
        val foregroundInfoProvider = mockk<CommunityUploadForegroundInfoProvider>(relaxed = true)
        coEvery { userUseCases.getCurrentUserId() } returns DataResourceResult.Failure(Exception("로그인이 필요합니다."))
        val worker = communityUploadWorker(
            userUseCases = userUseCases,
            uploadProcessor = uploadProcessor,
            foregroundInfoProvider = foregroundInfoProvider
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.failure()::class.java)
        verify(exactly = 0) { foregroundInfoProvider.create(any()) }
        coVerify(exactly = 0) { uploadProcessor.upload(any()) }
    }

    private fun communityUploadWorker(
        context: Context = mockk(relaxed = true),
        inputData: Data = communityUploadData(),
        workId: UUID = UUID.randomUUID(),
        foregroundUpdater: ForegroundUpdater = mockk(relaxed = true),
        userUseCases: UserUseCases = mockk(relaxed = true),
        uploadProcessor: CommunityUploadProcessor = mockk(relaxed = true),
        foregroundInfoProvider: CommunityUploadForegroundInfoProvider = mockk(relaxed = true),
        runAttemptCount: Int = 0
    ): CommunityUploadWorker {
        return CommunityUploadWorker(
            appContext = context,
            workerParams = workerParameters(
                inputData = inputData,
                workId = workId,
                foregroundUpdater = foregroundUpdater,
                runAttemptCount = runAttemptCount
            ),
            userUseCases = userUseCases,
            uploadProcessor = uploadProcessor,
            foregroundInfoProvider = foregroundInfoProvider
        )
    }

    private fun workerParameters(
        inputData: Data,
        workId: UUID,
        foregroundUpdater: ForegroundUpdater,
        runAttemptCount: Int
    ): WorkerParameters {
        val workerParameters = mockk<WorkerParameters>(relaxed = true)
        every { workerParameters.inputData } returns inputData
        every { workerParameters.id } returns workId
        every { workerParameters.foregroundUpdater } returns foregroundUpdater
        every { workerParameters.runAttemptCount } returns runAttemptCount
        every { workerParameters.tags } returns emptySet()
        return workerParameters
    }

    private fun communityUploadData(): Data {
        return Data.Builder()
            .putString(CommunityUploadWorker.KEY_TITLE, "첫 커뮤니티 글")
            .putString(CommunityUploadWorker.KEY_CONTENT, "차 향이 아주 좋아서 공유해요")
            .putStringArray(CommunityUploadWorker.KEY_TAGS, arrayOf("우롱", "향"))
            .putStringArray(CommunityUploadWorker.KEY_IMAGE_URIS, arrayOf("file://post.jpg"))
            .putString(CommunityUploadWorker.KEY_POST_ID, "post-123")
            .putString(CommunityUploadWorker.KEY_LINKED_TEA_TYPE, "OOLONG")
            .putInt(CommunityUploadWorker.KEY_LINKED_RATING, 4)
            .build()
    }
}
