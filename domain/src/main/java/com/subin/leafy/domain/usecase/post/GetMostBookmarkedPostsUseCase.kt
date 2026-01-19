package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.RankingPeriod
import com.subin.leafy.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow

// 명예의 전당 (가장 많이 북마크된 글)
class GetMostBookmarkedPostsUseCase(private val repository: PostRepository) {
    operator fun invoke(
        period: RankingPeriod = RankingPeriod.ALL_TIME,
        limit: Int = 20
    ): Flow<DataResourceResult<List<CommunityPost>>> {
        return repository.getMostBookmarkedPosts(period, limit)
    }
}