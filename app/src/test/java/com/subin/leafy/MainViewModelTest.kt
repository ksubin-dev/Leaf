package com.subin.leafy

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.Operation
import androidx.work.WorkManager
import com.google.common.truth.Truth.assertThat
import com.leafy.shared.navigation.MainNavigationRoute
import com.subin.leafy.data.datasource.local.UploadQueueDataSource
import com.subin.leafy.data.worker.SyncWorker
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.UploadQueue
import com.subin.leafy.domain.model.UploadStatus
import com.subin.leafy.domain.model.UploadTargetType
import com.subin.leafy.domain.usecase.AuthUseCases
import com.subin.leafy.domain.usecase.SettingUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import com.subin.leafy.domain.usecase.note.RecoverQueuedNoteUploadsUseCase
import com.subin.leafy.domain.usecase.note.RetryFailedNoteUploadUseCase
import com.subin.leafy.domain.usecase.post.RecoverQueuedCommunityUploadsUseCase
import com.subin.leafy.domain.usecase.post.RetryFailedCommunityUploadUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `로그인 사용자이고 자동 로그인이 켜져 있으면 사용자별 초기 Sync를 KEEP 정책으로 예약한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val workManager = mockk<WorkManager>()
            val captured = captureUniqueWork(workManager)
            val recoverQueuedNoteUploads = mockk<RecoverQueuedNoteUploadsUseCase>(relaxed = true)
            val recoverQueuedCommunityUploads = mockk<RecoverQueuedCommunityUploadsUseCase>(relaxed = true)
            coEvery { recoverQueuedNoteUploads() } returns 2
            coEvery { recoverQueuedCommunityUploads() } returns 1
            val viewModel = mainViewModel(
                userIdResult = DataResourceResult.Success("user-123"),
                isAutoLoginEnabled = true,
                recoverQueuedNoteUploads = recoverQueuedNoteUploads,
                recoverQueuedCommunityUploads = recoverQueuedCommunityUploads,
                workManager = workManager
            )

            advanceUntilIdle()

            assertThat(captured.names).containsExactly("initial_sync_user-123")
            assertThat(captured.policies).containsExactly(ExistingWorkPolicy.KEEP)
            assertThat(captured.singleRequest().tags).contains(SyncWorker::class.java.name)
            coVerify(exactly = 1) { recoverQueuedNoteUploads() }
            coVerify(exactly = 1) { recoverQueuedCommunityUploads() }
            assertThat(viewModel.startDestination.value).isEqualTo(MainNavigationRoute.HomeTab)
            assertThat(viewModel.isSplashLoading.value).isFalse()
        }

    @Test
    fun `로그인 사용자지만 자동 로그인이 꺼져 있으면 로그아웃하고 Auth 화면으로 이동한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val workManager = mockk<WorkManager>(relaxed = true)
            val authUseCases = mockk<AuthUseCases>(relaxed = true)
            coEvery { authUseCases.logout() } returns DataResourceResult.Success(Unit)
            val viewModel = mainViewModel(
                userIdResult = DataResourceResult.Success("user-123"),
                isAutoLoginEnabled = false,
                authUseCases = authUseCases,
                workManager = workManager
            )

            advanceUntilIdle()

            verify(exactly = 0) {
                workManager.enqueueUniqueWork(any(), any<ExistingWorkPolicy>(), any<OneTimeWorkRequest>())
            }
            coVerify(exactly = 1) { authUseCases.logout() }
            assertThat(viewModel.startDestination.value).isEqualTo(MainNavigationRoute.Auth)
            assertThat(viewModel.isSplashLoading.value).isFalse()
        }

    @Test
    fun `로그인 사용자가 없으면 초기 Sync를 예약하지 않고 Auth 화면으로 이동한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val workManager = mockk<WorkManager>(relaxed = true)
            val authUseCases = mockk<AuthUseCases>(relaxed = true)
            val viewModel = mainViewModel(
                userIdResult = DataResourceResult.Failure(Exception("로그인된 사용자가 없습니다.")),
                isAutoLoginEnabled = true,
                authUseCases = authUseCases,
                workManager = workManager
            )

            advanceUntilIdle()

            verify(exactly = 0) {
                workManager.enqueueUniqueWork(any(), any<ExistingWorkPolicy>(), any<OneTimeWorkRequest>())
            }
            coVerify(exactly = 0) { authUseCases.logout() }
            assertThat(viewModel.startDestination.value).isEqualTo(MainNavigationRoute.Auth)
            assertThat(viewModel.isSplashLoading.value).isFalse()
        }

    @Test
    fun `업로드 수동 재시도는 targetType에 맞는 재시도 UseCase를 호출한다`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val retryFailedNoteUpload = mockk<RetryFailedNoteUploadUseCase>(relaxed = true)
            val retryFailedCommunityUpload = mockk<RetryFailedCommunityUploadUseCase>(relaxed = true)
            val uploadQueueDataSource = mockk<UploadQueueDataSource>()
            every { uploadQueueDataSource.observeLatestVisible() } returns flowOf(
                UploadQueue(
                    id = "NOTE_note-1",
                    targetType = UploadTargetType.NOTE,
                    targetId = "note-1",
                    status = UploadStatus.FAILED
                )
            )
            coEvery { retryFailedNoteUpload("NOTE_note-1") } returns DataResourceResult.Success(Unit)
            coEvery { retryFailedCommunityUpload("COMMUNITY_post-1") } returns DataResourceResult.Success(Unit)
            val viewModel = mainViewModel(
                userIdResult = DataResourceResult.Failure(Exception("로그인된 사용자가 없습니다.")),
                isAutoLoginEnabled = true,
                uploadQueueDataSource = uploadQueueDataSource,
                retryFailedNoteUpload = retryFailedNoteUpload,
                retryFailedCommunityUpload = retryFailedCommunityUpload
            )

            viewModel.retryUpload("NOTE_note-1", UploadTargetType.NOTE)
            viewModel.retryUpload("COMMUNITY_post-1", UploadTargetType.COMMUNITY)
            advanceUntilIdle()

            coVerify(exactly = 1) { retryFailedNoteUpload("NOTE_note-1") }
            coVerify(exactly = 1) { retryFailedCommunityUpload("COMMUNITY_post-1") }
        }

    private fun mainViewModel(
        userIdResult: DataResourceResult<String>,
        isAutoLoginEnabled: Boolean,
        authUseCases: AuthUseCases = mockk(relaxed = true),
        recoverQueuedNoteUploads: RecoverQueuedNoteUploadsUseCase = mockk(relaxed = true),
        recoverQueuedCommunityUploads: RecoverQueuedCommunityUploadsUseCase = mockk(relaxed = true),
        retryFailedNoteUpload: RetryFailedNoteUploadUseCase = mockk(relaxed = true),
        retryFailedCommunityUpload: RetryFailedCommunityUploadUseCase = mockk(relaxed = true),
        uploadQueueDataSource: UploadQueueDataSource = mockk(),
        workManager: WorkManager = mockk(relaxed = true)
    ): MainViewModel {
        val userUseCases = mockk<UserUseCases>(relaxed = true)
        val settingUseCases = mockk<SettingUseCases>(relaxed = true)

        every { userUseCases.getCurrentUserId() } returns userIdResult
        every { settingUseCases.manageLoginSetting.getAutoLogin() } returns flowOf(isAutoLoginEnabled)
        every { uploadQueueDataSource.observeLatestVisible() } returns flowOf(null)

        return MainViewModel(
            userUseCases = userUseCases,
            authUseCases = authUseCases,
            settingUseCases = settingUseCases,
            recoverQueuedNoteUploads = recoverQueuedNoteUploads,
            recoverQueuedCommunityUploads = recoverQueuedCommunityUploads,
            retryFailedNoteUpload = retryFailedNoteUpload,
            retryFailedCommunityUpload = retryFailedCommunityUpload,
            uploadQueueDataSource = uploadQueueDataSource,
            workManager = workManager
        )
    }

    private fun captureUniqueWork(workManager: WorkManager): CapturedUniqueWork {
        val captured = CapturedUniqueWork()
        val operation = mockk<Operation>(relaxed = true)

        every {
            workManager.enqueueUniqueWork(
                capture(captured.names),
                capture(captured.policies),
                capture(captured.requests)
            )
        } returns operation

        return captured
    }

    private class CapturedUniqueWork {
        val names = mutableListOf<String>()
        val policies = mutableListOf<ExistingWorkPolicy>()
        val requests = mutableListOf<OneTimeWorkRequest>()

        fun singleRequest(): OneTimeWorkRequest {
            assertThat(requests).hasSize(1)
            return requests.single()
        }
    }
}
