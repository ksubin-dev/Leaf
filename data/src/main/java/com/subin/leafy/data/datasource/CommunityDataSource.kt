package com.subin.leafy.data.datasource

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.CommunityTag
import com.subin.leafy.domain.model.TeaMaster

interface CommunityDataSource {
    suspend fun getPopularNotes(): DataResourceResult<List<CommunityPost>>
    suspend fun getRisingNotes(): DataResourceResult<List<CommunityPost>>
    suspend fun getMostSavedNotes(): DataResourceResult<List<CommunityPost>>
    suspend fun getRecommendedMasters(): DataResourceResult<List<TeaMaster>>
    suspend fun getPopularTags(): DataResourceResult<List<CommunityTag>>
    suspend fun getFollowingFeed(): DataResourceResult<List<CommunityPost>>
    suspend fun toggleLike(postId: String): DataResourceResult<Boolean>
    suspend fun toggleFollow(masterId: String): DataResourceResult<Boolean>
}