package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow

// 명예의 전당 (가장 많이 북마크된 글)
class GetMostBookmarkedPostsUseCase(
    private val postRepository: PostRepository
) {
    operator fun invoke(): Flow<DataResourceResult<List<CommunityPost>>> {
        return postRepository.getMostBookmarkedPosts()
    }
}