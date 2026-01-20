package com.subin.leafy.data.datasource.remote

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Comment
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.RankingPeriod
import com.subin.leafy.domain.model.TeaMaster
import com.subin.leafy.domain.model.TeaType
import kotlinx.coroutines.flow.Flow

interface PostDataSource {

    // --- 1. 피드 조회 (Read) ---

    // 이번 주 주간 랭킹 (홈 화면용)
    fun getWeeklyRanking(teaType: TeaType?): Flow<DataResourceResult<List<CommunityPost>>>

    // 이번 주 인기 노트 (조회수 + 좋아요 기준)
    fun getPopularPosts(limit: Int = 20): Flow<DataResourceResult<List<CommunityPost>>>

    // 가장 많이 북마크된 노트 (명예의 전당)
    fun getMostBookmarkedPosts(period: RankingPeriod, limit: Int): Flow<DataResourceResult<List<CommunityPost>>>

    // 팔로잉 피드 (내가 팔로우한 사람들의 글)
    fun getFollowingFeed(followingIds: List<String>): Flow<DataResourceResult<List<CommunityPost>>>

    suspend fun getPostsByIds(postIds: List<String>): DataResourceResult<List<CommunityPost>>

    fun getUserPosts(userId: String): Flow<DataResourceResult<List<CommunityPost>>>

    suspend fun getPostDetail(postId: String): DataResourceResult<CommunityPost>

    suspend fun searchPosts(
        query: String,
        lastPostId: String? = null,
        limit: Int = 20
    ): DataResourceResult<List<CommunityPost>>

    suspend fun createPost(post: CommunityPost): DataResourceResult<Unit>
    suspend fun updatePost(post: CommunityPost): DataResourceResult<Unit>
    suspend fun deletePost(postId: String): DataResourceResult<Unit>

    fun getComments(postId: String): Flow<DataResourceResult<List<Comment>>>
    suspend fun addComment(postId: String, comment: Comment): DataResourceResult<Unit>
    suspend fun deleteComment(postId: String, commentId: String): DataResourceResult<Unit>

    suspend fun toggleLike(postId: String, isLiked: Boolean, userId: String): DataResourceResult<Unit>
    suspend fun toggleBookmark(postId: String, isBookmarked: Boolean, userId: String): DataResourceResult<Unit>
    suspend fun incrementViewCount(postId: String): DataResourceResult<Unit>
}