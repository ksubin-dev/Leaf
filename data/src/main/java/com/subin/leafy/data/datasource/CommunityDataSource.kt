package com.subin.leafy.data.datasource

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.CommunityTag
import com.subin.leafy.domain.model.TeaMaster
import kotlinx.coroutines.flow.Flow

interface CommunityDataSource {
    fun getPopularNotes(): Flow<DataResourceResult<List<CommunityPost>>>
    fun getRisingNotes(): Flow<DataResourceResult<List<CommunityPost>>>
    fun getMostSavedNotes(): Flow<DataResourceResult<List<CommunityPost>>>
    fun getFollowingFeed(): Flow<DataResourceResult<List<CommunityPost>>>
    fun getRecommendedMasters(): Flow<DataResourceResult<List<TeaMaster>>>
    fun getPopularTags(): Flow<DataResourceResult<List<CommunityTag>>>
    suspend fun toggleLike(postId: String): DataResourceResult<Boolean>
    suspend fun toggleFollow(masterId: String): DataResourceResult<Boolean>
}