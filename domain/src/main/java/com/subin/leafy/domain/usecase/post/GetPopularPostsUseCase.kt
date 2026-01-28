package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPopularPostsUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    operator fun invoke(limit: Int = 20): Flow<DataResourceResult<List<CommunityPost>>> {
        return postRepository.getPopularPosts(limit)
    }
}