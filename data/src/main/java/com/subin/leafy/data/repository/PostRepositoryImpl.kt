package com.subin.leafy.data.repository

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.WorkManager
import com.google.gson.Gson
import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.data.datasource.local.UploadQueueDataSource
import com.subin.leafy.data.worker.CommunityUploadWorker
import com.subin.leafy.data.datasource.remote.AuthDataSource
import com.subin.leafy.data.datasource.remote.PostDataSource
import com.subin.leafy.data.datasource.remote.TeaMasterDataSource
import com.subin.leafy.data.datasource.remote.UserDataSource
import com.subin.leafy.data.mapper.toRankingItem
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.*
import com.subin.leafy.domain.repository.PostChangeEvent
import com.subin.leafy.domain.repository.PostRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.security.MessageDigest
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class PostRepositoryImpl @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val uploadQueueDataSource: UploadQueueDataSource,
    private val postDataSource: PostDataSource,
    private val userDataSource: UserDataSource,
    private val teaMasterDataSource: TeaMasterDataSource,
    private val imageCompressor: ImageCompressor,
    private val workManager: WorkManager
) : PostRepository {

    private val gson = Gson()

    private val _postChangeFlow = MutableSharedFlow<PostChangeEvent>()
    override val postChangeFlow: Flow<PostChangeEvent> = _postChangeFlow.asSharedFlow()

    override fun getWeeklyRanking(teaType: TeaType?): Flow<DataResourceResult<List<RankingItem>>> {
        return postDataSource.getWeeklyRanking(teaType).map { result ->
            when (result) {
                is DataResourceResult.Success -> {
                    val filteredPosts = result.data.filter { it.teaType != TeaType.ETC }
                    val rankingItems = filteredPosts.mapIndexed { index, post ->
                        post.toRankingItem(rank = index + 1)
                    }

                    DataResourceResult.Success(rankingItems)
                }
                is DataResourceResult.Failure -> DataResourceResult.Failure(result.exception)
                else -> DataResourceResult.Failure(Exception("Unknown State"))
            }
        }
    }

    override fun getPopularPosts(limit: Int): Flow<DataResourceResult<List<CommunityPost>>> {
        return postDataSource.getPopularPosts(limit).combineWithUser()
    }

    override fun getMostBookmarkedPosts(period: RankingPeriod, limit: Int): Flow<DataResourceResult<List<CommunityPost>>> {
        return postDataSource.getMostBookmarkedPosts(period, limit).combineWithUser()
    }

    override fun getFollowingFeed(followingIds: List<String>): Flow<DataResourceResult<List<CommunityPost>>> {
        if (followingIds.isEmpty()) return flowOf(DataResourceResult.Success(emptyList()))
        return postDataSource.getFollowingFeed(followingIds).combineWithUser()
    }

    override fun getUserPosts(userId: String): Flow<DataResourceResult<List<CommunityPost>>> {
        return postDataSource.getUserPosts(userId).combineWithUser()
    }

    override fun getMyLikedPosts(): Flow<DataResourceResult<List<CommunityPost>>> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return flowOf(DataResourceResult.Failure(Exception("로그인이 필요합니다.")))

        return userDataSource.getUserFlow(myUid).flatMapLatest { userResult ->
            if (userResult is DataResourceResult.Success) {
                val likedIds = userResult.data.likedPostIds

                if (likedIds.isEmpty()) {
                    flowOf(DataResourceResult.Success(emptyList()))
                } else {
                    flow {
                        val postsResult = postDataSource.getPostsByIds(likedIds)
                        emit(mapPostsWithMyStateInternal(postsResult, userResult.data))
                    }
                }
            } else {
                flowOf(DataResourceResult.Failure(Exception("유저 정보 로드 실패")))
            }
        }
    }

    override fun getMyBookmarkedPosts(): Flow<DataResourceResult<List<CommunityPost>>> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return flowOf(DataResourceResult.Failure(Exception("로그인이 필요합니다.")))

        return userDataSource.getUserFlow(myUid).flatMapLatest { userResult ->
            if (userResult is DataResourceResult.Success) {
                val bookmarkedIds = userResult.data.bookmarkedPostIds

                if (bookmarkedIds.isEmpty()) {
                    flowOf(DataResourceResult.Success(emptyList()))
                } else {
                    flow {
                        val postsResult = postDataSource.getPostsByIds(bookmarkedIds)
                        emit(mapPostsWithMyStateInternal(postsResult, userResult.data))
                    }
                }
            } else {
                flowOf(DataResourceResult.Failure(Exception("유저 정보 로드 실패")))
            }
        }
    }

    override suspend fun getPostDetail(postId: String): DataResourceResult<CommunityPost> {
        val result = postDataSource.getPostDetail(postId)
        return if (result is DataResourceResult.Success) {
            val mappedPost = mapSinglePostWithMyState(result.data)
            DataResourceResult.Success(mappedPost)
        } else {
            result
        }
    }

    override suspend fun searchPosts(
        query: String,
        lastPostId: String?,
        limit: Int
    ): DataResourceResult<List<CommunityPost>> {
        val result = postDataSource.searchPosts(query, lastPostId, limit)
        return mapPostsWithMyStateSuspend(result)
    }

    override fun getRecommendedMasters(limit: Int): Flow<DataResourceResult<List<TeaMaster>>> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return teaMasterDataSource.getRecommendedMasters(limit)

        return combine(
            teaMasterDataSource.getRecommendedMasters(limit),
            userDataSource.getFollowingIdsFlow(myUid)
        ) { masterResult, followingResult ->
            if (masterResult !is DataResourceResult.Success) return@combine masterResult

            val masters = masterResult.data
            val followingIds = if (followingResult is DataResourceResult.Success) followingResult.data else emptyList()

            val mappedMasters = masters.map { master ->
                master.copy(isFollowing = followingIds.contains(master.id))
            }
            DataResourceResult.Success(mappedMasters)
        }
    }

    override suspend fun createPost(
        postId: String, title: String, content: String, imageUrls: List<String>,
        teaType: String?, rating: Int?, tags: List<String>, brewingSummary: String?, originNoteId: String?
    ): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId() ?: return DataResourceResult.Failure(Exception("로그인 필요"))
        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) return DataResourceResult.Failure(Exception("유저 정보 오류"))
        if (imageUrls.hasLocalImageUris()) {
            return DataResourceResult.Failure(Exception("원격 저장 전 이미지 업로드가 필요합니다."))
        }
        val me = userResult.data

        val newPost = CommunityPost(
            id = postId,
            author = PostAuthor(me.id, me.nickname, me.profileImageUrl, isFollowing = false),
            imageUrls = imageUrls, title = title, content = content, originNoteId = originNoteId,
            teaType = teaType?.let { runCatching { TeaType.valueOf(it) }.getOrNull() },
            rating = rating, tags = tags, brewingSummary = brewingSummary,
            stats = PostStatistics(0, 0, 0, 0),
            myState = PostSocialState(false, false),
            createdAt = System.currentTimeMillis()
        )
        return postDataSource.createPost(newPost)
    }

    override suspend fun updatePost(post: CommunityPost) = postDataSource.updatePost(post)
    override suspend fun deletePost(postId: String) = postDataSource.deletePost(postId)

    override fun getComments(postId: String): Flow<DataResourceResult<List<Comment>>> {
        val myUid = authDataSource.getCurrentUserId()
        return postDataSource.getComments(postId).map { result ->
            if (result is DataResourceResult.Success) {
                val mappedComments = result.data.map { comment ->
                    comment.copy(isMine = comment.author.id == myUid)
                }
                DataResourceResult.Success(mappedComments)
            } else {
                result
            }
        }
    }

    override suspend fun addComment(postId: String, content: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId() ?: return DataResourceResult.Failure(Exception("로그인 필요"))
        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) return DataResourceResult.Failure(Exception("유저 정보 오류"))
        val me = userResult.data

        val newComment = Comment(
            id = "", postId = postId,
            author = CommentAuthor(me.id, me.nickname, me.profileImageUrl),
            content = content, createdAt = System.currentTimeMillis(), isMine = true
        )
        return postDataSource.addComment(postId, newComment)
    }

    override suspend fun deleteComment(postId: String, commentId: String) = postDataSource.deleteComment(postId, commentId)

    override suspend fun toggleLike(postId: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) return DataResourceResult.Failure(Exception("User error"))

        val isCurrentlyLiked = userResult.data.likedPostIds.contains(postId)
        val newIsLiked = !isCurrentlyLiked

        val result = postDataSource.toggleLike(postId, newIsLiked, myUid)

        if (result is DataResourceResult.Success) {
            _postChangeFlow.emit(PostChangeEvent.Like(postId, newIsLiked))
        }

        return result
    }

    override suspend fun toggleBookmark(postId: String): DataResourceResult<Unit> {
        val myUid = authDataSource.getCurrentUserId()
            ?: return DataResourceResult.Failure(Exception("로그인이 필요합니다."))

        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) return DataResourceResult.Failure(Exception("User error"))

        val isCurrentlyBookmarked = userResult.data.bookmarkedPostIds.contains(postId)
        val newIsBookmarked = !isCurrentlyBookmarked

        val result = postDataSource.toggleBookmark(postId, newIsBookmarked, myUid)

        if (result is DataResourceResult.Success) {
            _postChangeFlow.emit(PostChangeEvent.Bookmark(postId, newIsBookmarked))
        }

        return result
    }

    override suspend fun incrementViewCount(postId: String) = postDataSource.incrementViewCount(postId)

    private fun Flow<DataResourceResult<List<CommunityPost>>>.combineWithUser(): Flow<DataResourceResult<List<CommunityPost>>> {
        val myUid = authDataSource.getCurrentUserId() ?: return this

        return combine(this, userDataSource.getUserFlow(myUid)) { postResult, userResult ->
            if (postResult is DataResourceResult.Success && userResult is DataResourceResult.Success) {
                mapPostsWithMyStateInternal(postResult, userResult.data)
            } else {
                postResult
            }
        }
    }

    override suspend fun schedulePostUpload(
        title: String,
        content: String,
        tags: List<String>,
        imageUriStrings: List<String>,
        linkedNoteId: String?,
        linkedTeaType: String?,
        linkedRating: Int?
    ) {
        val draftKey = createDraftKey(
            title,
            content,
            tags,
            imageUriStrings,
            linkedNoteId,
            linkedTeaType,
            linkedRating
        )
        val postId = "post_$draftKey"
        val queueId = uploadQueueId(UploadTargetType.COMMUNITY, postId)
        val durableImageUriStrings = imageUriStrings.toDurableCommunityImageUris(postId)
        val payload = CommunityUploadPayload(
            postId = postId,
            draftKey = draftKey,
            title = title,
            content = content,
            tags = tags,
            imageUriStrings = durableImageUriStrings,
            linkedNoteId = linkedNoteId,
            linkedTeaType = linkedTeaType,
            linkedRating = linkedRating
        )

        uploadQueueDataSource.upsert(
            UploadQueue(
                id = queueId,
                targetType = UploadTargetType.COMMUNITY,
                targetId = postId,
                status = UploadStatus.PENDING,
                payload = gson.toJson(payload),
                message = "백그라운드 업로드 대기 중입니다."
            )
        )

        enqueueCommunityUpload(
            queueId = queueId,
            payload = payload,
            existingWorkPolicy = ExistingWorkPolicy.KEEP
        )
    }

    override suspend fun recoverQueuedCommunityUploads(): Int {
        val queues = uploadQueueDataSource.getByTargetTypeAndStatuses(
            targetType = UploadTargetType.COMMUNITY,
            statuses = COMMUNITY_AUTO_RECOVERY_STATUSES
        )

        var recoveredCount = 0
        queues.forEach { queue ->
            val payload = queue.toCommunityUploadPayload()
            if (payload == null) {
                uploadQueueDataSource.updateStatus(
                    id = queue.id,
                    status = UploadStatus.FAILED,
                    attempt = queue.attempt,
                    message = "업로드 요청 정보가 올바르지 않습니다.",
                    lastError = "Invalid community upload payload"
                )
                return@forEach
            }

            uploadQueueDataSource.updateStatus(
                id = queue.id,
                status = UploadStatus.PENDING,
                attempt = 0,
                message = "업로드 대기 중입니다.",
                lastError = queue.lastError
            )
            enqueueCommunityUpload(
                queueId = queue.id,
                payload = payload,
                existingWorkPolicy = ExistingWorkPolicy.KEEP
            )
            recoveredCount++
        }

        return recoveredCount
    }

    private fun enqueueCommunityUpload(
        queueId: String,
        payload: CommunityUploadPayload,
        existingWorkPolicy: ExistingWorkPolicy
    ) {
        val inputData = Data.Builder()
            .putString(CommunityUploadWorker.KEY_UPLOAD_QUEUE_ID, queueId)
            .putString(CommunityUploadWorker.KEY_TITLE, payload.title)
            .putString(CommunityUploadWorker.KEY_CONTENT, payload.content)
            .putStringArray(CommunityUploadWorker.KEY_TAGS, payload.tags.toTypedArray())
            .putStringArray(CommunityUploadWorker.KEY_IMAGE_URIS, payload.imageUriStrings.toTypedArray())
            .putString(CommunityUploadWorker.KEY_POST_ID, payload.postId)
            .putString(CommunityUploadWorker.KEY_LINKED_NOTE_ID, payload.linkedNoteId)
            .putString(CommunityUploadWorker.KEY_LINKED_TEA_TYPE, payload.linkedTeaType)
            .putInt(CommunityUploadWorker.KEY_LINKED_RATING, payload.linkedRating ?: -1)
            .build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest = OneTimeWorkRequestBuilder<CommunityUploadWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .addTag("upload_community")
            .addTag("upload_post_${payload.postId}")
            .build()

        workManager.enqueueUniqueWork(
            payload.uniqueWorkName(),
            existingWorkPolicy,
            uploadRequest
        )
    }

    private fun uploadQueueId(targetType: UploadTargetType, targetId: String): String {
        return "${targetType.name}_$targetId"
    }

    private fun UploadQueue.toCommunityUploadPayload(): CommunityUploadPayload? {
        return payload?.let {
            runCatching { gson.fromJson(it, CommunityUploadPayload::class.java) }.getOrNull()
        }
    }

    private suspend fun List<String>.toDurableCommunityImageUris(postId: String): List<String> {
        return mapIndexed { index, uriString ->
            if (uriString.startsWith("http")) {
                uriString
            } else {
                imageCompressor.saveImageToInternalStorage(
                    imageUriString = uriString,
                    folderName = "posts/$postId",
                    filePrefix = "post_$index"
                )
            }
        }
    }

    private fun List<String>.hasLocalImageUris(): Boolean {
        return any { !it.startsWith("http") }
    }

    private fun createDraftKey(
        title: String,
        content: String,
        tags: List<String>,
        imageUriStrings: List<String>,
        linkedNoteId: String?,
        linkedTeaType: String?,
        linkedRating: Int?
    ): String {
        val source = listOf(
            title,
            content,
            tags.joinToString(separator = ","),
            imageUriStrings.joinToString(separator = ","),
            linkedNoteId.orEmpty(),
            linkedTeaType.orEmpty(),
            linkedRating?.toString().orEmpty()
        ).joinToString(separator = "|")

        return MessageDigest
            .getInstance("SHA-256")
            .digest(source.toByteArray())
            .joinToString(separator = "") { "%02x".format(it) }
            .take(16)
    }

    private fun mapPostsWithMyStateInternal(
        postResult: DataResourceResult<List<CommunityPost>>,
        me: User
    ): DataResourceResult<List<CommunityPost>> {
        if (postResult !is DataResourceResult.Success) return postResult

        val mappedPosts = postResult.data.map { post ->
            post.copy(
                myState = PostSocialState(
                    isLiked = me.likedPostIds.contains(post.id),
                    isBookmarked = me.bookmarkedPostIds.contains(post.id)
                )
            )
        }
        return DataResourceResult.Success(mappedPosts)
    }

    private suspend fun mapPostsWithMyStateSuspend(
        result: DataResourceResult<List<CommunityPost>>
    ): DataResourceResult<List<CommunityPost>> {
        if (result !is DataResourceResult.Success) return result
        val myUid = authDataSource.getCurrentUserId() ?: return result
        val userResult = userDataSource.getUser(myUid)
        if (userResult !is DataResourceResult.Success) return result

        return mapPostsWithMyStateInternal(result, userResult.data)
    }

    private suspend fun mapSinglePostWithMyState(post: CommunityPost): CommunityPost {
        val myUid = authDataSource.getCurrentUserId() ?: return post
        val userResult = userDataSource.getUser(myUid)

        return if (userResult is DataResourceResult.Success) {
            val me = userResult.data
            post.copy(
                myState = PostSocialState(
                    isLiked = me.likedPostIds.contains(post.id),
                    isBookmarked = me.bookmarkedPostIds.contains(post.id)
                )
            )
        } else {
            post
        }
    }

    private data class CommunityUploadPayload(
        val postId: String,
        val draftKey: String? = null,
        val title: String,
        val content: String,
        val tags: List<String> = emptyList(),
        val imageUriStrings: List<String> = emptyList(),
        val linkedNoteId: String? = null,
        val linkedTeaType: String? = null,
        val linkedRating: Int? = null
    ) {
        fun uniqueWorkName(): String {
            val key = draftKey ?: postId.removePrefix("post_")
            return "upload_post_draft_$key"
        }
    }

    private companion object {
        val COMMUNITY_AUTO_RECOVERY_STATUSES = listOf(
            UploadStatus.PENDING,
            UploadStatus.RETRYING
        )
    }
}
