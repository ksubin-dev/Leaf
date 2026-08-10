package com.subin.leafy.data.worker

import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.truth.Truth.assertThat
import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.data.datasource.local.UploadQueueDataSource
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CommunityUploadProcessorTest {

    @Test
    fun `Community Worker는 postId가 누락되면 foreground 실행 전에 failure를 반환한다`() = runTest {
        val userUseCases = mockk<UserUseCases>(relaxed = true)
        val uploadProcessor = mockk<CommunityUploadProcessor>(relaxed = true)
        coEvery { userUseCases.getCurrentUserId() } returns DataResourceResult.Success("user-123")
        val worker = communityUploadWorker(
            inputData = communityUploadData(postId = null),
            userUseCases = userUseCases,
            uploadProcessor = uploadProcessor
        )

        val result = worker.doWork()

        assertThat(result).isInstanceOf(ListenableWorker.Result.failure()::class.java)
        coVerify(exactly = 0) { uploadProcessor.upload(any()) }
    }

    @Test
    fun `Community Processor는 로컬 이미지 업로드 성공 시 postId 기반 Storage URL로 게시글을 생성한다`() = runTest {
        val postUseCases = mockk<PostUseCases>(relaxed = true)
        val imageUseCases = mockk<ImageUseCases>(relaxed = true)
        val imageCompressor = mockk<ImageCompressor>(relaxed = true)
        coEvery { imageCompressor.compressImage("file://post-1.jpg") } returns "compressed-post-1.jpg"
        coEvery { imageCompressor.compressImage("file://post-2.jpg") } returns "compressed-post-2.jpg"
        coEvery {
            imageUseCases.uploadImage("compressed-post-1.jpg", "posts/user-123/post-123/image_0.jpg")
        } returns DataResourceResult.Success("https://storage.example.com/post-1.jpg")
        coEvery {
            imageUseCases.uploadImage("compressed-post-2.jpg", "posts/user-123/post-123/image_1.jpg")
        } returns DataResourceResult.Success("https://storage.example.com/post-2.jpg")
        coEvery {
            postUseCases.createPost(
                postId = "post-123",
                title = "첫 커뮤니티 글",
                content = "차 향이 아주 좋아서 공유해요",
                imageUrls = listOf(
                    "https://storage.example.com/post-1.jpg",
                    "https://storage.example.com/post-2.jpg"
                ),
                teaType = "OOLONG",
                rating = 4,
                tags = listOf("우롱", "향"),
                brewingSummary = null,
                originNoteId = null
            )
        } returns DataResourceResult.Success(Unit)
        val processor = communityUploadProcessor(postUseCases, imageUseCases, imageCompressor)

        val result = processor.upload(
            communityUploadRequest(
                imageUriStrings = listOf("file://post-1.jpg", "file://post-2.jpg")
            )
        )

        assertThat(result).isInstanceOf(DataResourceResult.Success::class.java)
        coVerify(exactly = 1) {
            postUseCases.createPost(
                postId = "post-123",
                title = "첫 커뮤니티 글",
                content = "차 향이 아주 좋아서 공유해요",
                imageUrls = listOf(
                    "https://storage.example.com/post-1.jpg",
                    "https://storage.example.com/post-2.jpg"
                ),
                teaType = "OOLONG",
                rating = 4,
                tags = listOf("우롱", "향"),
                brewingSummary = null,
                originNoteId = null
            )
        }
    }

    @Test
    fun `Community Processor는 기존 http 이미지 URL을 재업로드하지 않고 게시글을 생성한다`() = runTest {
        val postUseCases = mockk<PostUseCases>(relaxed = true)
        val imageUseCases = mockk<ImageUseCases>(relaxed = true)
        val imageCompressor = mockk<ImageCompressor>(relaxed = true)
        val remoteUrl = "https://storage.example.com/existing-post.jpg"
        coEvery {
            postUseCases.createPost(
                postId = "post-123",
                title = "첫 커뮤니티 글",
                content = "차 향이 아주 좋아서 공유해요",
                imageUrls = listOf(remoteUrl),
                teaType = "OOLONG",
                rating = 4,
                tags = listOf("우롱", "향"),
                brewingSummary = null,
                originNoteId = null
            )
        } returns DataResourceResult.Success(Unit)
        val processor = communityUploadProcessor(postUseCases, imageUseCases, imageCompressor)

        val result = processor.upload(communityUploadRequest(imageUriStrings = listOf(remoteUrl)))

        assertThat(result).isInstanceOf(DataResourceResult.Success::class.java)
        coVerify(exactly = 0) { imageCompressor.compressImage(any()) }
        coVerify(exactly = 0) { imageUseCases.uploadImage(any(), any()) }
    }

    @Test
    fun `Community Processor는 노트 공유 시 postId 기반 경로로 이미지를 업로드하고 shareNoteAsPost를 호출한다`() = runTest {
        val postUseCases = mockk<PostUseCases>(relaxed = true)
        val imageUseCases = mockk<ImageUseCases>(relaxed = true)
        val imageCompressor = mockk<ImageCompressor>(relaxed = true)
        coEvery { imageCompressor.compressImage("file://share.jpg") } returns "compressed-share.jpg"
        coEvery {
            imageUseCases.uploadImage("compressed-share.jpg", "posts/user-123/post-123/image_0.jpg")
        } returns DataResourceResult.Success("https://storage.example.com/share.jpg")
        coEvery {
            postUseCases.shareNoteAsPost(
                noteId = "note-123",
                content = "차 향이 아주 좋아서 공유해요",
                tags = listOf("우롱", "향"),
                imageUrls = listOf("https://storage.example.com/share.jpg"),
                postId = "post-123"
            )
        } returns DataResourceResult.Success(Unit)
        val processor = communityUploadProcessor(postUseCases, imageUseCases, imageCompressor)

        val result = processor.upload(
            communityUploadRequest(
                imageUriStrings = listOf("file://share.jpg"),
                linkedNoteId = "note-123"
            )
        )

        assertThat(result).isInstanceOf(DataResourceResult.Success::class.java)
        coVerify(exactly = 1) {
            imageUseCases.uploadImage("compressed-share.jpg", "posts/user-123/post-123/image_0.jpg")
        }
        coVerify(exactly = 1) {
            postUseCases.shareNoteAsPost(
                noteId = "note-123",
                content = "차 향이 아주 좋아서 공유해요",
                tags = listOf("우롱", "향"),
                imageUrls = listOf("https://storage.example.com/share.jpg"),
                postId = "post-123"
            )
        }
        coVerify(exactly = 0) {
            postUseCases.createPost(any(), any(), any(), any(), any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `Community Processor는 이미지 업로드 실패 시 게시글을 생성하지 않고 실패를 반환한다`() = runTest {
        val postUseCases = mockk<PostUseCases>(relaxed = true)
        val imageUseCases = mockk<ImageUseCases>(relaxed = true)
        val imageCompressor = mockk<ImageCompressor>(relaxed = true)
        val exception = Exception("Firebase Storage timeout")
        coEvery { imageCompressor.compressImage("file://post.jpg") } returns "compressed-post.jpg"
        coEvery {
            imageUseCases.uploadImage("compressed-post.jpg", "posts/user-123/post-123/image_0.jpg")
        } returns DataResourceResult.Failure(exception)
        val processor = communityUploadProcessor(postUseCases, imageUseCases, imageCompressor)

        val result = processor.upload(communityUploadRequest(imageUriStrings = listOf("file://post.jpg")))

        assertThat(result).isInstanceOf(DataResourceResult.Failure::class.java)
        assertThat((result as DataResourceResult.Failure).exception).isSameInstanceAs(exception)
        coVerify(exactly = 0) {
            postUseCases.createPost(any(), any(), any(), any(), any(), any(), any(), any(), any())
        }
        coVerify(exactly = 0) { postUseCases.shareNoteAsPost(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `Community Processor는 createPost 실패를 그대로 반환한다`() = runTest {
        val postUseCases = mockk<PostUseCases>(relaxed = true)
        val exception = Exception("Firebase unavailable")
        coEvery {
            postUseCases.createPost(
                postId = "post-123",
                title = "첫 커뮤니티 글",
                content = "차 향이 아주 좋아서 공유해요",
                imageUrls = listOf("https://storage.example.com/post.jpg"),
                teaType = "OOLONG",
                rating = 4,
                tags = listOf("우롱", "향"),
                brewingSummary = null,
                originNoteId = null
            )
        } returns DataResourceResult.Failure(exception)
        val processor = communityUploadProcessor(postUseCases = postUseCases)

        val result = processor.upload(
            communityUploadRequest(
                imageUriStrings = listOf("https://storage.example.com/post.jpg")
            )
        )

        assertThat(result).isInstanceOf(DataResourceResult.Failure::class.java)
        assertThat((result as DataResourceResult.Failure).exception).isSameInstanceAs(exception)
    }

    @Test
    fun `Community Processor는 shareNoteAsPost 실패를 그대로 반환한다`() = runTest {
        val postUseCases = mockk<PostUseCases>(relaxed = true)
        val exception = Exception("Firebase unavailable")
        coEvery {
            postUseCases.shareNoteAsPost(
                noteId = "note-123",
                content = "차 향이 아주 좋아서 공유해요",
                tags = listOf("우롱", "향"),
                imageUrls = listOf("https://storage.example.com/share.jpg"),
                postId = "post-123"
            )
        } returns DataResourceResult.Failure(exception)
        val processor = communityUploadProcessor(postUseCases = postUseCases)

        val result = processor.upload(
            communityUploadRequest(
                imageUriStrings = listOf("https://storage.example.com/share.jpg"),
                linkedNoteId = "note-123"
            )
        )

        assertThat(result).isInstanceOf(DataResourceResult.Failure::class.java)
        assertThat((result as DataResourceResult.Failure).exception).isSameInstanceAs(exception)
    }

    private fun communityUploadWorker(
        inputData: Data,
        userUseCases: UserUseCases = mockk(relaxed = true),
        uploadProcessor: CommunityUploadProcessor = mockk(relaxed = true),
        foregroundInfoProvider: CommunityUploadForegroundInfoProvider = mockk(relaxed = true),
        runAttemptCount: Int = 0
    ): CommunityUploadWorker {
        return CommunityUploadWorker(
            appContext = mockk(relaxed = true),
            workerParams = workerParameters(inputData, runAttemptCount),
            userUseCases = userUseCases,
            uploadProcessor = uploadProcessor,
            foregroundInfoProvider = foregroundInfoProvider,
            uploadQueueDataSource = mockk<UploadQueueDataSource>(relaxed = true)
        )
    }

    private fun communityUploadProcessor(
        postUseCases: PostUseCases = mockk(relaxed = true),
        imageUseCases: ImageUseCases = mockk(relaxed = true),
        imageCompressor: ImageCompressor = mockk(relaxed = true)
    ): CommunityUploadProcessor {
        return CommunityUploadProcessor(
            postUseCases = postUseCases,
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

    private fun communityUploadData(
        postId: String? = "post-123"
    ): Data {
        val builder = Data.Builder()
            .putString(CommunityUploadWorker.KEY_TITLE, "첫 커뮤니티 글")
            .putString(CommunityUploadWorker.KEY_CONTENT, "차 향이 아주 좋아서 공유해요")
            .putStringArray(CommunityUploadWorker.KEY_TAGS, arrayOf("우롱", "향"))
            .putStringArray(CommunityUploadWorker.KEY_IMAGE_URIS, arrayOf("file://post.jpg"))
            .putString(CommunityUploadWorker.KEY_LINKED_TEA_TYPE, "OOLONG")
            .putInt(CommunityUploadWorker.KEY_LINKED_RATING, 4)

        if (postId != null) {
            builder.putString(CommunityUploadWorker.KEY_POST_ID, postId)
        }

        return builder.build()
    }

    private fun communityUploadRequest(
        imageUriStrings: List<String>,
        linkedNoteId: String? = null
    ): CommunityUploadRequest {
        return CommunityUploadRequest(
            userId = "user-123",
            postId = "post-123",
            title = "첫 커뮤니티 글",
            content = "차 향이 아주 좋아서 공유해요",
            tags = listOf("우롱", "향"),
            imageUriStrings = imageUriStrings,
            linkedNoteId = linkedNoteId,
            linkedTeaType = "OOLONG",
            linkedRating = 4
        )
    }
}
