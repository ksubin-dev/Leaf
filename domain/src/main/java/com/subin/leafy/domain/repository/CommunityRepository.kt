package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.*
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    // --- [1. 피드 및 게시글 조회] ---
    fun getPopularNotes(): Flow<DataResourceResult<List<CommunityPost>>>
    fun getMostSavedNotes(): Flow<DataResourceResult<List<CommunityPost>>>
    fun getFollowingFeed(): Flow<DataResourceResult<List<CommunityPost>>>

    // --- [2. 마스터 ] ---
    fun getRecommendedMasters(): Flow<DataResourceResult<List<TeaMaster>>>

    // --- [3. 댓글 기능] ---
    fun getComments(postId: String): Flow<DataResourceResult<List<Comment>>>
    suspend fun addComment(postId: String, content: String): DataResourceResult<Unit>
    suspend fun deleteComment(commentId: String, postId: String): DataResourceResult<Unit>

    // --- [4. 소셜 인터랙션] ---
    suspend fun incrementViewCount(postId: String): DataResourceResult<Unit>
    suspend fun toggleLike(postId: String, currentStatus: Boolean): DataResourceResult<Unit>
    suspend fun toggleSave(postId: String, currentStatus: Boolean): DataResourceResult<Unit>
    suspend fun toggleFollow(masterId: String, currentStatus: Boolean): DataResourceResult<Unit>
}