package com.subin.leafy.domain.repository

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.CommunityTag
import com.subin.leafy.domain.model.TeaMaster
import kotlinx.coroutines.flow.Flow

interface CommunityRepository {
    // Trending 탭용 데이터
    fun getPopularNotes(): Flow<DataResourceResult<List<CommunityPost>>>
    fun getRisingNotes(): Flow<DataResourceResult<List<CommunityPost>>>
    fun getMostSavedNotes(): Flow<DataResourceResult<List<CommunityPost>>>
    fun getRecommendedMasters(): Flow<DataResourceResult<List<TeaMaster>>>
    fun getPopularTags(): Flow<DataResourceResult<List<CommunityTag>>>

    // Following 탭용 데이터
    fun getFollowingFeed(): Flow<DataResourceResult<List<CommunityPost>>>

    // 액션 (결과값만 필요하므로 suspend 유지 혹은 Flow 선택 가능)
    suspend fun toggleLike(postId: String): DataResourceResult<Boolean>
    suspend fun toggleFollow(masterId: String): DataResourceResult<Boolean>
}