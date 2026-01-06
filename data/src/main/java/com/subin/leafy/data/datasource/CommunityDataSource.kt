package com.subin.leafy.data.datasource

import com.subin.leafy.data.model.dto.CommentDTO
import com.subin.leafy.data.model.dto.CommunityPostDTO
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.*
import kotlinx.coroutines.flow.Flow

interface CommunityDataSource {
    fun getPopularNotes(): Flow<DataResourceResult<List<CommunityPost>>>
    fun getMostSavedNotes(): Flow<DataResourceResult<List<CommunityPost>>>
    fun getFollowingFeed(followingIds: List<String>): Flow<DataResourceResult<List<CommunityPost>>>
    fun getRecommendedMasters(): Flow<DataResourceResult<List<TeaMaster>>>


    // 댓글 관련
    fun getComments(postId: String): Flow<DataResourceResult<List<CommentDTO>>>
    suspend fun addComment(userId: String, userName: String, userProfile: String?, postId: String, content: String): DataResourceResult<Unit>
    suspend fun deleteComment(commentId: String, postId: String): DataResourceResult<Unit>

    // 소셜 액션 (인자명 통일)
    suspend fun toggleLike(userId: String, postId: String, isLiked: Boolean): DataResourceResult<Unit>
    suspend fun toggleSave(userId: String, postId: String, isSaved: Boolean): DataResourceResult<Unit>
    suspend fun toggleFollow(userId: String, masterId: String, isFollowing: Boolean): DataResourceResult<Unit>
    suspend fun incrementViewCount(postId: String): DataResourceResult<Unit>
}