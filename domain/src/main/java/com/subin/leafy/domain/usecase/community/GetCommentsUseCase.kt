package com.subin.leafy.domain.usecase.community

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Comment
import com.subin.leafy.domain.repository.CommunityRepository
import kotlinx.coroutines.flow.Flow

class GetCommentsUseCase(
    private val repository: CommunityRepository
) {
    operator fun invoke(postId: String): Flow<DataResourceResult<List<Comment>>> {
        return repository.getComments(postId)
    }
}