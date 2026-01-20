package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.repository.PostRepository

class SearchPostsUseCase(private val postRepository: PostRepository) {
    suspend operator fun invoke(
        query: String,
        lastPostId: String? = null,
        limit: Int = 20
    ): DataResourceResult<List<CommunityPost>> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) return DataResourceResult.Success(emptyList())

        return postRepository.searchPosts(trimmedQuery, lastPostId, limit)
    }
}