package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.RankingItem
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeeklyRankingUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    operator fun invoke(teaType: TeaType?): Flow<DataResourceResult<List<RankingItem>>> {
        return postRepository.getWeeklyRanking(teaType)
    }
}