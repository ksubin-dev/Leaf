package com.subin.leafy.data.repository

import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.Operation
import androidx.work.WorkManager
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.data.datasource.local.LocalNoteDataSource
import com.subin.leafy.data.datasource.local.LocalTeaDataSource
import com.subin.leafy.data.datasource.local.UploadQueueDataSource
import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.PostDataSource
import com.subin.leafy.data.datasource.remote.RemoteNoteDataSource
import com.subin.leafy.data.datasource.remote.RemoteTeaDataSource
import com.subin.leafy.data.datasource.remote.TeaMasterDataSource
import com.subin.leafy.data.datasource.remote.UserDataSource
import com.subin.leafy.data.worker.CommunityUploadWorker
import com.subin.leafy.data.worker.ProfileUploadWorker
import com.subin.leafy.data.worker.TeaUploadWorker
import com.subin.leafy.data.worker.UploadWorker
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.BrewingRecipe
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserRelationState
import com.subin.leafy.domain.model.UserSocialStatistics
import com.subin.leafy.domain.model.NoteMetadata
import com.subin.leafy.domain.model.PostSocialState
import com.subin.leafy.domain.model.PostStatistics
import com.subin.leafy.domain.model.RatingInfo
import com.subin.leafy.domain.model.SensoryEvaluation
import com.subin.leafy.domain.model.TeaInfo
import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.model.TeawareType
import com.subin.leafy.domain.model.UploadQueue
import com.subin.leafy.domain.model.UploadStatus
import com.subin.leafy.domain.model.UploadTargetType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WorkManagerEnqueuePolicyTest {

    private val gson = Gson()

    @Test
    fun `Note 업로드는 noteId 기준 REPLACE unique work로 예약한다`() = runTest {
        val workManager = mockk<WorkManager>()
        val captured = captureUniqueWork(workManager)
        val uploadQueueDataSource = mockk<UploadQueueDataSource>(relaxed = true)
        val imageCompressor = mockk<ImageCompressor>()
        val repository = NoteRepositoryImpl(
            localNoteDataSource = mockk(relaxed = true),
            uploadQueueDataSource = uploadQueueDataSource,
            remoteNoteDataSource = mockk(),
            authDataSource = mockk(),
            userDataSource = mockk(),
            imageCompressor = imageCompressor,
            workManager = workManager
        )
        val note = brewingNote(id = "note-123")
        val imageUris = listOf("file://first.jpg", "https://example.com/second.jpg")
        val durableImageUris = listOf("file://internal-first.jpg", "https://example.com/second.jpg")
        coEvery {
            imageCompressor.saveImageToInternalStorage("file://first.jpg", "notes/note-123", "note_0")
        } returns durableImageUris[0]

        repository.scheduleNoteUpload(note, imageUris, isEditMode = true)

        val request = captured.singleRequest()
        assertThat(captured.names).containsExactly("upload_note_note-123")
        assertThat(captured.policies).containsExactly(ExistingWorkPolicy.REPLACE)
        assertThat(request.tags).contains("upload_note_note-123")
        assertThat(request.workSpec.constraints.requiredNetworkType).isEqualTo(NetworkType.CONNECTED)
        assertThat(request.workSpec.input.getBoolean(UploadWorker.KEY_IS_EDIT_MODE, false)).isTrue()

        val noteJson = request.workSpec.input.getString(UploadWorker.KEY_NOTE_DATA)
        val imagesJson = request.workSpec.input.getString(UploadWorker.KEY_IMAGE_URIS)
        val parsedNote = gson.fromJson(noteJson, BrewingNote::class.java)
        val parsedImages: List<String> = gson.fromJson(imagesJson, object : TypeToken<List<String>>() {}.type)

        assertThat(parsedNote.id).isEqualTo("note-123")
        assertThat(parsedImages).containsExactlyElementsIn(durableImageUris).inOrder()
        coVerify(exactly = 1) {
            uploadQueueDataSource.upsert(
                match {
                    it.id == "NOTE_note-123" &&
                        it.targetType == UploadTargetType.NOTE &&
                        it.targetId == "note-123" &&
                        it.status == UploadStatus.PENDING &&
                        it.message == "백그라운드 업로드 대기 중입니다."
                }
            )
        }
    }

    @Test
    fun `Tea 업로드는 teaId 기준 REPLACE unique work로 예약한다`() = runTest {
        val workManager = mockk<WorkManager>()
        val captured = captureUniqueWork(workManager)
        val authDataSource = mockk<AuthDataSource>()
        every { authDataSource.getCurrentUserId() } returns "user-123"
        val repository = TeaRepositoryImpl(
            localTeaDataSource = mockk(),
            remoteTeaDataSource = mockk(),
            authDataSource = authDataSource,
            workManager = workManager
        )

        repository.scheduleTeaUpload(teaItem(id = "tea-123"), "file://tea.jpg")

        val request = captured.singleRequest()
        assertThat(captured.names).containsExactly("upload_tea_tea-123")
        assertThat(captured.policies).containsExactly(ExistingWorkPolicy.REPLACE)
        assertThat(request.tags).contains("upload_tea_tea-123")
        assertThat(request.workSpec.constraints.requiredNetworkType).isEqualTo(NetworkType.CONNECTED)
        assertThat(request.workSpec.input.getString(TeaUploadWorker.KEY_IMAGE_URI)).isEqualTo("file://tea.jpg")

        val teaJson = request.workSpec.input.getString(TeaUploadWorker.KEY_TEA_DATA)
        val parsedTea = gson.fromJson(teaJson, TeaItem::class.java)
        assertThat(parsedTea.id).isEqualTo("tea-123")
        assertThat(parsedTea.ownerId).isEqualTo("user-123")
    }

    @Test
    fun `Tea 업로드는 로그인 사용자가 없으면 예약하지 않는다`() = runTest {
        val workManager = mockk<WorkManager>(relaxed = true)
        val authDataSource = mockk<AuthDataSource>()
        every { authDataSource.getCurrentUserId() } returns null
        val repository = TeaRepositoryImpl(
            localTeaDataSource = mockk(),
            remoteTeaDataSource = mockk(),
            authDataSource = authDataSource,
            workManager = workManager
        )

        repository.scheduleTeaUpload(teaItem(id = "tea-123"), "file://tea.jpg")

        verify(exactly = 0) {
            workManager.enqueueUniqueWork(any(), any<ExistingWorkPolicy>(), any<OneTimeWorkRequest>())
        }
    }

    @Test
    fun `Profile 업데이트는 userId 기준 REPLACE unique work로 예약한다`() = runTest {
        val workManager = mockk<WorkManager>()
        val captured = captureUniqueWork(workManager)
        val repository = UserRepositoryImpl(
            authDataSource = mockk(),
            userDataSource = mockk(),
            workManager = workManager
        )

        repository.scheduleProfileUpdate(
            nickname = "티타임조아",
            bio = "차를 마시는 순간을 기록합니다",
            imageUriString = "file://profile.jpg",
            userId = "user-123"
        )

        val request = captured.singleRequest()
        assertThat(captured.names).containsExactly("update_profile_user-123")
        assertThat(captured.policies).containsExactly(ExistingWorkPolicy.REPLACE)
        assertThat(request.tags).contains("update_profile")
        assertThat(request.workSpec.constraints.requiredNetworkType).isEqualTo(NetworkType.CONNECTED)
        assertThat(request.workSpec.input.getString(ProfileUploadWorker.KEY_USER_ID)).isEqualTo("user-123")
        assertThat(request.workSpec.input.getString(ProfileUploadWorker.KEY_NICKNAME)).isEqualTo("티타임조아")
        assertThat(request.workSpec.input.getString(ProfileUploadWorker.KEY_BIO)).isEqualTo("차를 마시는 순간을 기록합니다")
        assertThat(request.workSpec.input.getString(ProfileUploadWorker.KEY_IMAGE_URI)).isEqualTo("file://profile.jpg")
    }

    @Test
    fun `Community 업로드는 draft 기준 KEEP unique work로 예약하고 postId를 inputData와 tag에 고정한다`() = runTest {
        val workManager = mockk<WorkManager>()
        val captured = captureUniqueWork(workManager)
        val uploadQueueDataSource = mockk<UploadQueueDataSource>(relaxed = true)
        val imageCompressor = mockk<ImageCompressor>()
        val repository = postRepository(workManager, uploadQueueDataSource, imageCompressor)
        coEvery {
            imageCompressor.saveImageToInternalStorage(any(), any(), any())
        } returnsMany listOf("file://internal-post-0.jpg", "file://internal-post-1.jpg")

        repository.schedulePostUpload(
            title = "가끔 먹는 커피",
            content = "물론 차가 더 좋지만 가끔 좋죠",
            tags = listOf("#가끔먹는커피", "#커피"),
            imageUriStrings = listOf("file://post_0.jpg", "file://post_1.jpg"),
            linkedNoteId = null,
            linkedTeaType = TeaType.ETC.name,
            linkedRating = 4
        )

        val request = captured.singleRequest()
        val postId = request.workSpec.input.getString(CommunityUploadWorker.KEY_POST_ID)

        assertThat(captured.names.single()).startsWith("upload_post_draft_")
        assertThat(captured.policies).containsExactly(ExistingWorkPolicy.KEEP)
        assertThat(postId).isNotNull()
        assertThat(postId).isNotEmpty()
        assertThat(request.tags).contains("upload_community")
        assertThat(request.tags).contains("upload_post_$postId")
        assertThat(request.workSpec.constraints.requiredNetworkType).isEqualTo(NetworkType.CONNECTED)
        assertThat(request.workSpec.input.getString(CommunityUploadWorker.KEY_TITLE)).isEqualTo("가끔 먹는 커피")
        assertThat(request.workSpec.input.getString(CommunityUploadWorker.KEY_CONTENT)).isEqualTo("물론 차가 더 좋지만 가끔 좋죠")
        assertThat(request.workSpec.input.getStringArray(CommunityUploadWorker.KEY_TAGS)?.toList())
            .containsExactly("#가끔먹는커피", "#커피")
            .inOrder()
        assertThat(request.workSpec.input.getStringArray(CommunityUploadWorker.KEY_IMAGE_URIS)?.toList())
            .containsExactly("file://internal-post-0.jpg", "file://internal-post-1.jpg")
            .inOrder()
        assertThat(request.workSpec.input.getString(CommunityUploadWorker.KEY_LINKED_TEA_TYPE)).isEqualTo(TeaType.ETC.name)
        assertThat(request.workSpec.input.getInt(CommunityUploadWorker.KEY_LINKED_RATING, -1)).isEqualTo(4)
        coVerify(exactly = 1) {
            uploadQueueDataSource.upsert(
                match {
                    it.id == "COMMUNITY_$postId" &&
                        it.targetType == UploadTargetType.COMMUNITY &&
                        it.targetId == postId &&
                        it.status == UploadStatus.PENDING &&
                        it.payload?.contains("file://internal-post-0.jpg") == true &&
                        it.message == "백그라운드 업로드 대기 중입니다."
                }
            )
        }
    }

    @Test
    fun `Community 업로드는 같은 draft 요청이면 같은 uniqueName으로 예약한다`() = runTest {
        val workManager = mockk<WorkManager>()
        val captured = captureUniqueWork(workManager)
        val imageCompressor = mockk<ImageCompressor>()
        val repository = postRepository(workManager, imageCompressor = imageCompressor)
        coEvery {
            imageCompressor.saveImageToInternalStorage("file://same.jpg", any(), "post_0")
        } returns "file://internal-same.jpg"

        repeat(2) {
            repository.schedulePostUpload(
                title = "같은 게시글",
                content = "중복 클릭으로 같은 내용이 들어온 상황",
                tags = listOf("#중복방지"),
                imageUriStrings = listOf("file://same.jpg"),
                linkedNoteId = "note-123",
                linkedTeaType = TeaType.BLACK.name,
                linkedRating = 5
            )
        }

        val firstRequest = captured.requests[0]
        val secondRequest = captured.requests[1]
        val firstPostId = firstRequest.workSpec.input.getString(CommunityUploadWorker.KEY_POST_ID)
        val secondPostId = secondRequest.workSpec.input.getString(CommunityUploadWorker.KEY_POST_ID)

        assertThat(captured.names).hasSize(2)
        assertThat(captured.names[0]).isEqualTo(captured.names[1])
        assertThat(captured.policies).containsExactly(ExistingWorkPolicy.KEEP, ExistingWorkPolicy.KEEP).inOrder()
        assertThat(firstPostId).isNotNull()
        assertThat(secondPostId).isNotNull()
        assertThat(firstRequest.tags).contains("upload_post_$firstPostId")
        assertThat(secondRequest.tags).contains("upload_post_$secondPostId")
    }

    @Test
    fun `Community 앱 재실행 복구는 PENDING 또는 RETRYING 큐를 KEEP 정책으로 재등록한다`() = runTest {
        val workManager = mockk<WorkManager>()
        val captured = captureUniqueWork(workManager)
        val uploadQueueDataSource = mockk<UploadQueueDataSource>(relaxed = true)
        val repository = postRepository(workManager, uploadQueueDataSource)
        val queue = UploadQueue(
            id = "COMMUNITY_post-draft-123",
            targetType = UploadTargetType.COMMUNITY,
            targetId = "post-draft-123",
            status = UploadStatus.RETRYING,
            payload = gson.toJson(
                mapOf(
                    "postId" to "post-draft-123",
                    "draftKey" to "draft-123",
                    "title" to "복구할 글",
                    "content" to "앱 재실행 후 업로드",
                    "tags" to listOf("복구"),
                    "imageUriStrings" to listOf("file://internal-post.jpg"),
                    "linkedNoteId" to null,
                    "linkedTeaType" to TeaType.OOLONG.name,
                    "linkedRating" to 4
                )
            )
        )
        coEvery {
            uploadQueueDataSource.getByTargetTypeAndStatuses(
                UploadTargetType.COMMUNITY,
                listOf(UploadStatus.PENDING, UploadStatus.RETRYING)
            )
        } returns listOf(queue)

        val recoveredCount = repository.recoverQueuedCommunityUploads()

        assertThat(recoveredCount).isEqualTo(1)
        coVerify(exactly = 1) {
            uploadQueueDataSource.updateStatus(
                id = "COMMUNITY_post-draft-123",
                status = UploadStatus.PENDING,
                attempt = 0,
                message = "업로드 대기 중입니다.",
                lastError = null
            )
        }
        assertThat(captured.names).containsExactly("upload_post_draft_draft-123")
        assertThat(captured.policies).containsExactly(ExistingWorkPolicy.KEEP)
        val request = captured.singleRequest()
        assertThat(request.workSpec.input.getString(CommunityUploadWorker.KEY_POST_ID)).isEqualTo("post-draft-123")
        assertThat(request.workSpec.input.getStringArray(CommunityUploadWorker.KEY_IMAGE_URIS)?.toList())
            .containsExactly("file://internal-post.jpg")
    }

    @Test
    fun `Community 앱 재실행 복구는 깨진 payload를 FAILED로 남기고 재등록하지 않는다`() = runTest {
        val workManager = mockk<WorkManager>(relaxed = true)
        val uploadQueueDataSource = mockk<UploadQueueDataSource>(relaxed = true)
        val repository = postRepository(workManager, uploadQueueDataSource)
        coEvery {
            uploadQueueDataSource.getByTargetTypeAndStatuses(
                UploadTargetType.COMMUNITY,
                listOf(UploadStatus.PENDING, UploadStatus.RETRYING)
            )
        } returns listOf(
            UploadQueue(
                id = "COMMUNITY_broken",
                targetType = UploadTargetType.COMMUNITY,
                targetId = "broken",
                status = UploadStatus.PENDING,
                payload = "{"
            )
        )

        val recoveredCount = repository.recoverQueuedCommunityUploads()

        assertThat(recoveredCount).isEqualTo(0)
        coVerify(exactly = 1) {
            uploadQueueDataSource.updateStatus(
                id = "COMMUNITY_broken",
                status = UploadStatus.FAILED,
                attempt = 0,
                message = "업로드 요청 정보가 올바르지 않습니다.",
                lastError = "Invalid community upload payload"
            )
        }
        verify(exactly = 0) {
            workManager.enqueueUniqueWork(any(), any<ExistingWorkPolicy>(), any<OneTimeWorkRequest>())
        }
    }

    @Test
    fun `Community 앱 재실행 복구는 FAILED와 AUTH_REQUIRED 큐를 자동 재등록 대상에서 제외한다`() = runTest {
        val workManager = mockk<WorkManager>(relaxed = true)
        val uploadQueueDataSource = mockk<UploadQueueDataSource>(relaxed = true)
        val repository = postRepository(workManager, uploadQueueDataSource)
        coEvery {
            uploadQueueDataSource.getByTargetTypeAndStatuses(
                UploadTargetType.COMMUNITY,
                listOf(UploadStatus.PENDING, UploadStatus.RETRYING)
            )
        } returns emptyList()

        val recoveredCount = repository.recoverQueuedCommunityUploads()

        assertThat(recoveredCount).isEqualTo(0)
        coVerify(exactly = 1) {
            uploadQueueDataSource.getByTargetTypeAndStatuses(
                UploadTargetType.COMMUNITY,
                listOf(UploadStatus.PENDING, UploadStatus.RETRYING)
            )
        }
        verify(exactly = 0) {
            workManager.enqueueUniqueWork(any(), any<ExistingWorkPolicy>(), any<OneTimeWorkRequest>())
        }
    }

    @Test
    fun `Community 원격 저장은 로컬 이미지 URI가 남아 있으면 Firestore 저장을 차단한다`() = runTest {
        val authDataSource = mockk<AuthDataSource>()
        val userDataSource = mockk<UserDataSource>()
        val postDataSource = mockk<PostDataSource>(relaxed = true)
        val repository = postRepository(
            workManager = mockk(relaxed = true),
            authDataSource = authDataSource,
            userDataSource = userDataSource,
            postDataSource = postDataSource
        )
        every { authDataSource.getCurrentUserId() } returns "user-123"
        coEvery { userDataSource.getUser("user-123") } returns DataResourceResult.Success(user())

        val result = repository.createPost(
            postId = "post-local-uri",
            title = "로컬 이미지 차단",
            content = "Worker 업로드 전에 원격 저장하면 안 된다",
            imageUrls = listOf("file://internal-post.jpg"),
            teaType = TeaType.BLACK.name,
            rating = 5,
            tags = listOf("#테스트"),
            brewingSummary = null,
            originNoteId = null
        )

        assertThat(result).isInstanceOf(DataResourceResult.Failure::class.java)
        assertThat((result as DataResourceResult.Failure).exception.message)
            .isEqualTo("원격 저장 전 이미지 업로드가 필요합니다.")
        coVerify(exactly = 0) { postDataSource.createPost(any()) }
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

    private fun postRepository(
        workManager: WorkManager,
        uploadQueueDataSource: UploadQueueDataSource = mockk(relaxed = true),
        imageCompressor: ImageCompressor = mockk(relaxed = true),
        authDataSource: AuthDataSource = mockk(),
        postDataSource: PostDataSource = mockk(),
        userDataSource: UserDataSource = mockk(),
        teaMasterDataSource: TeaMasterDataSource = mockk()
    ): PostRepositoryImpl {
        return PostRepositoryImpl(
            authDataSource = authDataSource,
            uploadQueueDataSource = uploadQueueDataSource,
            postDataSource = postDataSource,
            userDataSource = userDataSource,
            teaMasterDataSource = teaMasterDataSource,
            imageCompressor = imageCompressor,
            workManager = workManager
        )
    }

    private fun brewingNote(id: String): BrewingNote {
        return BrewingNote(
            id = id,
            ownerId = "owner-before-enqueue",
            teaInfo = TeaInfo(
                name = "아메리카노",
                brand = "카페",
                type = TeaType.ETC
            ),
            recipe = BrewingRecipe(
                waterTemp = 90,
                leafAmount = 3f,
                waterAmount = 150,
                brewTimeSeconds = 180,
                infusionCount = 1,
                teaware = TeawareType.MUG
            ),
            evaluation = SensoryEvaluation(),
            rating = RatingInfo(stars = 3),
            metadata = NoteMetadata(),
            stats = PostStatistics(),
            myState = PostSocialState(),
            date = 1L,
            createdAt = 1L
        )
    }

    private fun teaItem(id: String): TeaItem {
        return TeaItem(
            id = id,
            ownerId = "owner-before-enqueue",
            name = "우롱차",
            brand = "Leafy",
            type = TeaType.OOLONG
        )
    }

    private fun user(): User {
        return User(
            id = "user-123",
            nickname = "티타임조아",
            profileImageUrl = null,
            bio = null,
            socialStats = UserSocialStatistics(),
            relationState = UserRelationState(),
            followingIds = emptyList(),
            likedPostIds = emptyList(),
            bookmarkedPostIds = emptyList(),
            createdAt = 1L
        )
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
