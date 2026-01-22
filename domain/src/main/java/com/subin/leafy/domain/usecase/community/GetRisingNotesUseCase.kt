package com.subin.leafy.domain.usecase.community

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow

class GetRisingNotesUseCase(private val repository: CommunityRepository) {
    operator fun invoke(): Flow<DataResourceResult<List<CommunityPost>>> {
        return repository.getRisingNotes()
    }
}