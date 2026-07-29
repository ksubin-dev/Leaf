package com.subin.leafy.data.worker

import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.leafy.shared.utils.ImageCompressor
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
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkerImageUploadScenarioTest {

    private val gson = Gson()

    @Test
    fun `Note Worker는 로컬 이미지 업로드 성공 시 Storage URL로 note를 저장한다`() = runTest {
        val noteUseCases = mockk<NoteUseCases>(relaxed = true)
        val imageUseCases = mockk<ImageUseCases>(relaxed = true)
        val imageCompressor = mockk<ImageCompressor>(relaxed = true)
        val savedNote = slot<BrewingNote>()
        coEvery { imageCompressor.compressImage("file://note-1.jpg") } returns "compressed-note-1.jpg"
        coEvery { imageCompressor.compressImage("file://note-2.jpg") } returns "compressed-note-2.jpg"
        coEvery {
            imageUseCases.uploadImage("compressed-note-1.jpg", "notes/owner-123/note-123/image_0.jpg")
        } returns DataResourceResult.Success("https://storage.example.com/note-1.jpg")
        coEvery {
            imageUseCases.uploadImage("compressed-note-2.jpg", "notes/owner-123/note-123/image_1.jpg")
        } returns DataResourceResult.Success("https://storage.example.com/note-2.jpg")
        coEvery { noteUseCases.saveNote(capture(savedNote)) } returns DataResourceResult.Success(Unit)

        val worker = uploadWorker(
            inputData = noteUploadData(imageUris = listOf("file://note-1.jpg", "file://note-2.jpg")),
            noteUseCases = noteUseCases,
            imageUseCases = imageUseCases,
            imageCompressor = imageCompressor
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.success()::class.java)
        assertThat(savedNote.captured.metadata.imageUrls).containsExactly(
            "https://storage.example.com/note-1.jpg",
            "https://storage.example.com/note-2.jpg"
        ).inOrder()
        coVerify(exactly = 1) { noteUseCases.saveNote(any()) }
        coVerify(exactly = 0) { noteUseCases.updateNote(any()) }
    }

    @Test
    fun `Note Worker는 기존 http 이미지 URL을 재업로드하지 않고 수정 저장한다`() = runTest {
        val noteUseCases = mockk<NoteUseCases>(relaxed = true)
        val imageUseCases = mockk<ImageUseCases>(relaxed = true)
        val imageCompressor = mockk<ImageCompressor>(relaxed = true)
        val updatedNote = slot<BrewingNote>()
        val remoteUrl = "https://storage.example.com/existing-note.jpg"
        coEvery { noteUseCases.updateNote(capture(updatedNote)) } returns DataResourceResult.Success(Unit)

        val worker = uploadWorker(
            inputData = noteUploadData(imageUris = listOf(remoteUrl), isEditMode = true),
            noteUseCases = noteUseCases,
            imageUseCases = imageUseCases,
            imageCompressor = imageCompressor
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.success()::class.java)
        assertThat(updatedNote.captured.metadata.imageUrls).containsExactly(remoteUrl)
        coVerify(exactly = 0) { imageCompressor.compressImage(any()) }
        coVerify(exactly = 0) { imageUseCases.uploadImage(any(), any()) }
        coVerify(exactly = 0) { noteUseCases.saveNote(any()) }
        coVerify(exactly = 1) { noteUseCases.updateNote(any()) }
    }

    @Test
    fun `Note Worker는 신규 저장 실패가 재시도 가능하면 retry를 반환한다`() = runTest {
        val noteUseCases = mockk<NoteUseCases>(relaxed = true)
        coEvery { noteUseCases.saveNote(any()) } returns retryableFailure()
        val worker = uploadWorker(
            inputData = noteUploadData(),
            noteUseCases = noteUseCases,
            runAttemptCount = 0
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.retry()::class.java)
    }

    @Test
    fun `Note Worker는 수정 저장 실패가 최대 재시도에 도달하면 failure를 반환한다`() = runTest {
        val noteUseCases = mockk<NoteUseCases>(relaxed = true)
        coEvery { noteUseCases.updateNote(any()) } returns retryableFailure()
        val worker = uploadWorker(
            inputData = noteUploadData(isEditMode = true),
            noteUseCases = noteUseCases,
            runAttemptCount = MAX_RETRY_COUNT
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.failure()::class.java)
    }

    @Test
    fun `Tea Worker는 로컬 이미지 업로드 성공 시 imageUrl을 Storage URL로 저장한다`() = runTest {
        val teaUseCases = mockk<TeaUseCases>(relaxed = true)
        val imageUseCases = mockk<ImageUseCases>(relaxed = true)
        val imageCompressor = mockk<ImageCompressor>(relaxed = true)
        val savedTea = slot<TeaItem>()
        coEvery { imageCompressor.compressImage("file://tea.jpg") } returns "compressed-tea.jpg"
        coEvery {
            imageUseCases.uploadImage("compressed-tea.jpg", "teas/owner-123/tea-123.jpg")
        } returns DataResourceResult.Success("https://storage.example.com/tea.jpg")
        coEvery { teaUseCases.saveTea(capture(savedTea)) } returns DataResourceResult.Success(Unit)

        val worker = teaUploadWorker(
            inputData = teaUploadData(imageUri = "file://tea.jpg"),
            teaUseCases = teaUseCases,
            imageUseCases = imageUseCases,
            imageCompressor = imageCompressor
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.success()::class.java)
        assertThat(savedTea.captured.imageUrl).isEqualTo("https://storage.example.com/tea.jpg")
        coVerify(exactly = 1) { imageCompressor.compressImage("file://tea.jpg") }
        coVerify(exactly = 1) {
            imageUseCases.uploadImage("compressed-tea.jpg", "teas/owner-123/tea-123.jpg")
        }
    }

    @Test
    fun `Tea Worker는 기존 http 이미지 URL을 재업로드하지 않는다`() = runTest {
        val teaUseCases = mockk<TeaUseCases>(relaxed = true)
        val imageUseCases = mockk<ImageUseCases>(relaxed = true)
        val imageCompressor = mockk<ImageCompressor>(relaxed = true)
        val savedTea = slot<TeaItem>()
        val remoteUrl = "https://storage.example.com/existing-tea.jpg"
        coEvery { teaUseCases.saveTea(capture(savedTea)) } returns DataResourceResult.Success(Unit)

        val worker = teaUploadWorker(
            inputData = teaUploadData(imageUri = remoteUrl),
            teaUseCases = teaUseCases,
            imageUseCases = imageUseCases,
            imageCompressor = imageCompressor
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.success()::class.java)
        assertThat(savedTea.captured.imageUrl).isEqualTo(remoteUrl)
        coVerify(exactly = 0) { imageCompressor.compressImage(any()) }
        coVerify(exactly = 0) { imageUseCases.uploadImage(any(), any()) }
    }

    @Test
    fun `Tea Worker는 이미지 업로드 실패가 재시도 가능하면 retry를 반환한다`() = runTest {
        val imageUseCases = mockk<ImageUseCases>(relaxed = true)
        val imageCompressor = mockk<ImageCompressor>(relaxed = true)
        coEvery { imageCompressor.compressImage("file://tea.jpg") } returns "compressed-tea.jpg"
        coEvery {
            imageUseCases.uploadImage("compressed-tea.jpg", "teas/owner-123/tea-123.jpg")
        } returns retryableFailure()
        val worker = teaUploadWorker(
            inputData = teaUploadData(imageUri = "file://tea.jpg"),
            imageUseCases = imageUseCases,
            imageCompressor = imageCompressor,
            runAttemptCount = 0
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.retry()::class.java)
    }

    @Test
    fun `Tea Worker는 저장 실패가 최대 재시도에 도달하면 failure를 반환한다`() = runTest {
        val teaUseCases = mockk<TeaUseCases>(relaxed = true)
        coEvery { teaUseCases.saveTea(any()) } returns retryableFailure()
        val worker = teaUploadWorker(
            inputData = teaUploadData(),
            teaUseCases = teaUseCases,
            runAttemptCount = MAX_RETRY_COUNT
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.failure()::class.java)
    }

    @Test
    fun `Profile Worker는 로컬 이미지 업로드 성공 시 updateProfile에 Storage URL을 전달한다`() = runTest {
        val userUseCases = mockk<UserUseCases>(relaxed = true)
        val imageUseCases = mockk<ImageUseCases>(relaxed = true)
        val imageCompressor = mockk<ImageCompressor>(relaxed = true)
        coEvery { imageCompressor.compressImage("file://profile.jpg") } returns "compressed-profile.jpg"
        coEvery {
            imageUseCases.uploadImage("compressed-profile.jpg", "profile_images/user-123")
        } returns DataResourceResult.Success("https://storage.example.com/profile.jpg")
        coEvery {
            userUseCases.updateProfile(
                userId = "user-123",
                nickname = "리피",
                bio = "차 기록 중",
                profileUrl = "https://storage.example.com/profile.jpg"
            )
        } returns DataResourceResult.Success(Unit)

        val worker = profileUploadWorker(
            inputData = profileUploadData(
                bio = "차 기록 중",
                imageUri = "file://profile.jpg"
            ),
            userUseCases = userUseCases,
            imageUseCases = imageUseCases,
            imageCompressor = imageCompressor
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.success()::class.java)
        coVerify(exactly = 1) { imageCompressor.compressImage("file://profile.jpg") }
        coVerify(exactly = 1) {
            imageUseCases.uploadImage("compressed-profile.jpg", "profile_images/user-123")
        }
        coVerify(exactly = 1) {
            userUseCases.updateProfile(
                userId = "user-123",
                nickname = "리피",
                bio = "차 기록 중",
                profileUrl = "https://storage.example.com/profile.jpg"
            )
        }
    }

    @Test
    fun `Profile Worker는 기존 http 이미지 URL을 재업로드하지 않는다`() = runTest {
        val userUseCases = mockk<UserUseCases>(relaxed = true)
        val imageUseCases = mockk<ImageUseCases>(relaxed = true)
        val imageCompressor = mockk<ImageCompressor>(relaxed = true)
        val remoteUrl = "https://storage.example.com/existing-profile.jpg"
        coEvery {
            userUseCases.updateProfile(
                userId = "user-123",
                nickname = "리피",
                bio = "",
                profileUrl = remoteUrl
            )
        } returns DataResourceResult.Success(Unit)

        val worker = profileUploadWorker(
            inputData = profileUploadData(imageUri = remoteUrl),
            userUseCases = userUseCases,
            imageUseCases = imageUseCases,
            imageCompressor = imageCompressor
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.success()::class.java)
        coVerify(exactly = 0) { imageCompressor.compressImage(any()) }
        coVerify(exactly = 0) { imageUseCases.uploadImage(any(), any()) }
        coVerify(exactly = 1) {
            userUseCases.updateProfile(
                userId = "user-123",
                nickname = "리피",
                bio = "",
                profileUrl = remoteUrl
            )
        }
    }

    @Test
    fun `Profile Worker는 이미지 업로드 실패가 재시도 가능하면 retry를 반환한다`() = runTest {
        val imageUseCases = mockk<ImageUseCases>(relaxed = true)
        val imageCompressor = mockk<ImageCompressor>(relaxed = true)
        coEvery { imageCompressor.compressImage("file://profile.jpg") } returns "compressed-profile.jpg"
        coEvery {
            imageUseCases.uploadImage("compressed-profile.jpg", "profile_images/user-123")
        } returns retryableFailure()
        val worker = profileUploadWorker(
            inputData = profileUploadData(imageUri = "file://profile.jpg"),
            imageUseCases = imageUseCases,
            imageCompressor = imageCompressor,
            runAttemptCount = 0
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.retry()::class.java)
    }

    @Test
    fun `Profile Worker는 수정 실패가 최대 재시도에 도달하면 failure를 반환한다`() = runTest {
        val userUseCases = mockk<UserUseCases>(relaxed = true)
        coEvery {
            userUseCases.updateProfile(
                userId = "user-123",
                nickname = "리피",
                bio = "",
                profileUrl = null
            )
        } returns retryableFailure()
        val worker = profileUploadWorker(
            inputData = profileUploadData(),
            userUseCases = userUseCases,
            runAttemptCount = MAX_RETRY_COUNT
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.failure()::class.java)
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
            imageCompressor = imageCompressor
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

    private fun teaUploadData(
        tea: TeaItem = teaItem(),
        imageUri: String? = null
    ): Data {
        val builder = Data.Builder()
            .putString(TeaUploadWorker.KEY_TEA_DATA, gson.toJson(tea))

        if (imageUri != null) {
            builder.putString(TeaUploadWorker.KEY_IMAGE_URI, imageUri)
        }

        return builder.build()
    }

    private fun profileUploadData(
        userId: String = "user-123",
        nickname: String = "리피",
        bio: String? = null,
        imageUri: String? = null
    ): Data {
        val builder = Data.Builder()
            .putString(ProfileUploadWorker.KEY_USER_ID, userId)
            .putString(ProfileUploadWorker.KEY_NICKNAME, nickname)

        if (bio != null) {
            builder.putString(ProfileUploadWorker.KEY_BIO, bio)
        }

        if (imageUri != null) {
            builder.putString(ProfileUploadWorker.KEY_IMAGE_URI, imageUri)
        }

        return builder.build()
    }

    private fun retryableFailure(): DataResourceResult.Failure {
        return DataResourceResult.Failure(Exception("Firebase Storage timeout"))
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
