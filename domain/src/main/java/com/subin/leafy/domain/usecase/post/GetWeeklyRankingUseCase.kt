package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.RankingItem
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow

//랭킹조회
class GetWeeklyRankingUseCase(
    private val postRepository: PostRepository
) {
    operator fun invoke(teaType: TeaType?): Flow<DataResourceResult<List<RankingItem>>> {
        return postRepository.getWeeklyRanking(teaType)
    }
}