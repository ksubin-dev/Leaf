package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.repository.PostRepository

class SearchPostsUseCase(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(query: String): DataResourceResult<List<CommunityPost>> {
        // 1. 공백 제거
        val trimmedQuery = query.trim()
        // 2. 빈 값 체크
        if (trimmedQuery.isBlank()) {
            return DataResourceResult.Success(emptyList())
        }
        // 3. 깔끔해진 검색어로 요청
        return postRepository.searchPosts(trimmedQuery)
    }
}