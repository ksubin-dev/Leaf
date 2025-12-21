package com.subin.leafy.domain.usecase.community

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.TeaMaster
import com.subin.leafy.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow

class GetRecommendedMastersUseCase(private val repository: CommunityRepository) {
    operator fun invoke(): Flow<DataResourceResult<List<TeaMaster>>> {
        return repository.getRecommendedMasters()
    }
}