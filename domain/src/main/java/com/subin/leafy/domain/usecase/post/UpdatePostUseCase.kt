package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.CommunityPost
import com.subin.leafy.domain.repository.PostRepository

class UpdatePostUseCase(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(post: CommunityPost): DataResourceResult<Unit> {

        // 1. 이미지 검사
        if (post.imageUrls.isEmpty()) {
            return DataResourceResult.Failure(Exception("게시글에는 최소 1장의 사진이 필요합니다."))
        }

        // 2. 기본 데이터 검사
        val trimmedTitle = post.title.trim()
        val trimmedContent = post.content.trim()

        if (trimmedTitle.isBlank()) {
            return DataResourceResult.Failure(Exception("제목을 입력해주세요."))
        }
        if (trimmedTitle.length > 50) {
            return DataResourceResult.Failure(Exception("제목은 50자 이내로 작성해주세요."))
        }

        if (trimmedContent.isBlank()) {
            return DataResourceResult.Failure(Exception("내용을 입력해주세요."))
        }
        if (trimmedContent.length < 10) {
            return DataResourceResult.Failure(Exception("내용은 최소 10자 이상 작성해주세요."))
        }

        if (post.rating != null && post.rating !in 1..5) {
            return DataResourceResult.Failure(Exception("별점은 1점에서 5점 사이여야 합니다."))
        }

        // 3. 태그 정제 및 개수 제한
        val cleanedTags = post.tags.map { it.trim() }.filter { it.isNotBlank() }

        if (cleanedTags.size > 10) {
            return DataResourceResult.Failure(Exception("태그는 최대 10개까지만 등록 가능합니다."))
        }

        // 4. 정제된 데이터로 복사본 생성
        val validPost = post.copy(
            title = trimmedTitle,
            content = trimmedContent,
            tags = cleanedTags
        )

        return postRepository.updatePost(validPost)
    }
}