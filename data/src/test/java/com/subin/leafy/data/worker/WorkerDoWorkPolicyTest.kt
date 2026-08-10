package com.subin.leafy.data.worker

import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.data.datasource.local.UploadQueueDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.BrewingRecipe
import com.subin.leafy.domain.model.NoteMetadata
import com.subin.leafy.domain.model.PostSocialState
import com.subin.leafy.domain.model.PostStatistics
import com.subin.leafy.domain.model.RatingInfo
import com.subin.leafy.domain.model.SensoryEvaluation
import com.subin.leafy.domain.model.TeaInfo
import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.model.TeawareType
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.TeaUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkerDoWorkPolicyTest {

    private val gson = Gson()

    @Test
    fun `Note Worker는 필수 inputData가 누락되면 failure를 반환한다`() = runTest {
        val worker = uploadWorker(inputData = Data.EMPTY)

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.failure()::class.java)
    }

    @Test
    fun `Tea Worker는 이미지가 없어도 저장에 성공하면 success를 반환한다`() = runTest {
        val teaUseCases = mockk<TeaUseCases>(relaxed = true)
        coEvery { teaUseCases.saveTea(any()) } returns DataResourceResult.Success(Unit)
        val worker = teaUploadWorker(
            inputData = Data.Builder()
                .putString(TeaUploadWorker.KEY_TEA_DATA, gson.toJson(teaItem()))
                .build(),
            teaUseCases = teaUseCases
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.success()::class.java)
    }

    @Test
    fun `Note Worker는 이미지 업로드 실패가 재시도 가능하면 retry를 반환한다`() = runTest {
        val imageUseCases = mockk<ImageUseCases>(relaxed = true)
        val imageCompressor = mockk<ImageCompressor>(relaxed = true)
        coEvery { imageCompressor.compressImage("file://note.jpg") } returns "compressed-note.jpg"
        coEvery {
            imageUseCases.uploadImage("compressed-note.jpg", "notes/owner-123/note-123/image_0.jpg")
        } returns DataResourceResult.Failure(Exception("Firebase Storage timeout"))

        val worker = uploadWorker(
            inputData = noteUploadData(imageUris = listOf("file://note.jpg")),
            imageUseCases = imageUseCases,
            imageCompressor = imageCompressor,
            runAttemptCount = 0
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.retry()::class.java)
    }

    @Test
    fun `Note Worker는 이미지 업로드 실패가 최대 재시도에 도달하면 failure를 반환한다`() = runTest {
        val imageUseCases = mockk<ImageUseCases>(relaxed = true)
        val imageCompressor = mockk<ImageCompressor>(relaxed = true)
        coEvery { imageCompressor.compressImage("file://note.jpg") } returns "compressed-note.jpg"
        coEvery {
            imageUseCases.uploadImage("compressed-note.jpg", "notes/owner-123/note-123/image_0.jpg")
        } returns DataResourceResult.Failure(Exception("Firebase Storage timeout"))

        val worker = uploadWorker(
            inputData = noteUploadData(imageUris = listOf("file://note.jpg")),
            imageUseCases = imageUseCases,
            imageCompressor = imageCompressor,
            runAttemptCount = MAX_RETRY_COUNT
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.failure()::class.java)
    }

    @Test
    fun `Profile Worker는 userId나 nickname이 누락되면 failure를 반환한다`() = runTest {
        val worker = profileUploadWorker(
            inputData = Data.Builder()
                .putString(ProfileUploadWorker.KEY_USER_ID, "user-123")
                .build()
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.failure()::class.java)
    }

    @Test
    fun `Profile Worker는 이미지가 없어도 수정에 성공하면 success를 반환한다`() = runTest {
        val userUseCases = mockk<UserUseCases>(relaxed = true)
        coEvery {
            userUseCases.updateProfile(
                userId = "user-123",
                nickname = "리피",
                bio = "",
                profileUrl = null
            )
        } returns DataResourceResult.Success(Unit)
        val worker = profileUploadWorker(
            inputData = Data.Builder()
                .putString(ProfileUploadWorker.KEY_USER_ID, "user-123")
                .putString(ProfileUploadWorker.KEY_NICKNAME, "리피")
                .build(),
            userUseCases = userUseCases
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.success()::class.java)
    }

    private fun uploadWorker(
        inputData: Data,
        noteUseCases: NoteUseCases = mockk(relaxed = true),
        imageUseCases: ImageUseCases = mockk(relaxed = true),
        imageCompressor: ImageCompressor = mockk(relaxed = true),
        runAttemptCount: Int = 0
    ): UploadWorker {
        return UploadWorker(
            appContext = mockk(relaxed = true),
            workerParams = workerParameters(inputData, runAttemptCount),
            noteUseCases = noteUseCases,
            imageUseCases = imageUseCases,
            imageCompressor = imageCompressor,
            uploadQueueDataSource = mockk<UploadQueueDataSource>(relaxed = true)
        )
    }

    private fun teaUploadWorker(
        inputData: Data,
        teaUseCases: TeaUseCases = mockk(relaxed = true),
        imageUseCases: ImageUseCases = mockk(relaxed = true),
        imageCompressor: ImageCompressor = mockk(relaxed = true),
        runAttemptCount: Int = 0
    ): TeaUploadWorker {
        return TeaUploadWorker(
            appContext = mockk(relaxed = true),
            workerParams = workerParameters(inputData, runAttemptCount),
            teaUseCases = teaUseCases,
            imageUseCases = imageUseCases,
            imageCompressor = imageCompressor
        )
    }

    private fun profileUploadWorker(
        inputData: Data,
        userUseCases: UserUseCases = mockk(relaxed = true),
        imageUseCases: ImageUseCases = mockk(relaxed = true),
        imageCompressor: ImageCompressor = mockk(relaxed = true),
        runAttemptCount: Int = 0
    ): ProfileUploadWorker {
        return ProfileUploadWorker(
            appContext = mockk(relaxed = true),
            workerParams = workerParameters(inputData, runAttemptCount),
            userUseCases = userUseCases,
            imageUseCases = imageUseCases,
            imageCompressor = imageCompressor
        )
    }

    private fun workerParameters(inputData: Data, runAttemptCount: Int): WorkerParameters {
        val workerParameters = mockk<WorkerParameters>(relaxed = true)
        every { workerParameters.inputData } returns inputData
        every { workerParameters.runAttemptCount } returns runAttemptCount
        every { workerParameters.tags } returns emptySet()
        return workerParameters
    }

    private fun noteUploadData(
        note: BrewingNote = brewingNote(),
        imageUris: List<String> = emptyList(),
        isEditMode: Boolean = false
    ): Data {
        return Data.Builder()
            .putString(UploadWorker.KEY_NOTE_DATA, gson.toJson(note))
            .putString(UploadWorker.KEY_IMAGE_URIS, gson.toJson(imageUris))
            .putBoolean(UploadWorker.KEY_IS_EDIT_MODE, isEditMode)
            .build()
    }

    private fun brewingNote(): BrewingNote {
        return BrewingNote(
            id = "note-123",
            ownerId = "owner-123",
            teaInfo = TeaInfo(
                name = "우롱차",
                brand = "Leafy",
                type = TeaType.OOLONG
            ),
            recipe = BrewingRecipe(
                waterTemp = 90,
                leafAmount = 3f,
                waterAmount = 150,
                brewTimeSeconds = 180,
                infusionCount = 1,
                teaware = TeawareType.GAIWAN
            ),
            evaluation = SensoryEvaluation(),
            rating = RatingInfo(stars = 4),
            metadata = NoteMetadata(),
            stats = PostStatistics(),
            myState = PostSocialState(),
            date = 1L,
            createdAt = 1L
        )
    }

    private fun teaItem(): TeaItem {
        return TeaItem(
            id = "tea-123",
            ownerId = "owner-123",
            name = "우롱차",
            brand = "Leafy",
            type = TeaType.OOLONG
        )
    }
}
