package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaMaster
import com.subin.leafy.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow

class GetRecommendedMastersUseCase(
    private val postRepository: PostRepository
) {
    operator fun invoke(limit: Int = 10): Flow<DataResourceResult<List<TeaMaster>>> {
        return postRepository.getRecommendedMasters(limit)
    }
}