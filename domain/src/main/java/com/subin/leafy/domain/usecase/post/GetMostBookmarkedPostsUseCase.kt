package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.model.RankingPeriod
import com.subin.leafy.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetMostBookmarkedPostsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    operator fun invoke(
        period: RankingPeriod = RankingPeriod.ALL_TIME,
        limit: Int = 20
    ): Flow<DataResourceResult<List<CommunityPost>>> {
        return repository.getMostBookmarkedPosts(period, limit)
    }
}