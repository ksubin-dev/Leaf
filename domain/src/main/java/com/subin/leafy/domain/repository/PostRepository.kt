package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Comment
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.RankingItem
import com.subin.leafy.domain.model.RankingPeriod
import com.subin.leafy.domain.model.TeaMaster
import com.subin.leafy.domain.model.TeaType
import kotlinx.coroutines.flow.Flow

sealed class PostChangeEvent {
    abstract val postId: String

    data class Like(
        override val postId: String,
        val isLiked: Boolean
    ) : PostChangeEvent()

    data class Bookmark(
        override val postId: String,
        val isBookmarked: Boolean
    ) : PostChangeEvent()
}

interface PostRepository {

    fun getWeeklyRanking(teaType: TeaType?): Flow<DataResourceResult<List<RankingItem>>>

    fun getPopularPosts(limit: Int = 20): Flow<DataResourceResult<List<CommunityPost>>>

    fun getMostBookmarkedPosts(period: RankingPeriod = RankingPeriod.ALL_TIME, limit: Int = 20): Flow<DataResourceResult<List<CommunityPost>>>

    fun getFollowingFeed(followingIds: List<String>): Flow<DataResourceResult<List<CommunityPost>>>

    fun getUserPosts(userId: String): Flow<DataResourceResult<List<CommunityPost>>>

    suspend fun getPostDetail(postId: String): DataResourceResult<CommunityPost>

    suspend fun searchPosts(
        query: String,
        lastPostId: String? = null,
        limit: Int = 20
    ): DataResourceResult<List<CommunityPost>>

    fun getRecommendedMasters(limit: Int = 10): Flow<DataResourceResult<List<TeaMaster>>>

    suspend fun createPost(
        postId: String,
        title: String,
        content: String,
        imageUrls: List<String>,
        teaType: String?,
        rating: Int?,
        tags: List<String>,
        brewingSummary: String?,
        originNoteId: String? = null
    ): DataResourceResult<Unit>

    suspend fun updatePost(post: CommunityPost): DataResourceResult<Unit>

    suspend fun deletePost(postId: String): DataResourceResult<Unit>

    fun getComments(postId: String): Flow<DataResourceResult<List<Comment>>>

    suspend fun addComment(postId: String, content: String): DataResourceResult<Unit>

    suspend fun deleteComment(postId: String, commentId: String): DataResourceResult<Unit>

    val postChangeFlow: Flow<PostChangeEvent>

    suspend fun toggleLike(postId: String): DataResourceResult<Unit>

    suspend fun toggleBookmark(postId: String): DataResourceResult<Unit>

    suspend fun incrementViewCount(postId: String): DataResourceResult<Unit>

    fun getMyLikedPosts(): Flow<DataResourceResult<List<CommunityPost>>>

    fun getMyBookmarkedPosts(): Flow<DataResourceResult<List<CommunityPost>>>
}