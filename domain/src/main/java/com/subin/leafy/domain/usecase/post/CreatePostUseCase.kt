package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.PostRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(
        postId: String,
        title: String,
        content: String,
        imageUrls: List<String>,
        teaType: String?,
        rating: Int?,
        tags: List<String>,
        brewingSummary: String?,
        originNoteId: String? = null
    ): DataResourceResult<Unit> {

        if (imageUrls.isEmpty()) {
            return DataResourceResult.Failure(Exception("게시글에는 최소 1장의 사진이 필요합니다."))
        }
        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) {
            return DataResourceResult.Failure(Exception("제목을 입력해주세요."))
        }
        if (trimmedTitle.length > 50) {
            return DataResourceResult.Failure(Exception("제목은 50자 이내로 작성해주세요."))
        }

        val trimmedContent = content.trim()
        if (trimmedContent.isBlank()) {
            return DataResourceResult.Failure(Exception("내용을 입력해주세요."))
        }
        if (trimmedContent.length < 10) {
            return DataResourceResult.Failure(Exception("내용은 최소 10자 이상 작성해주세요."))
        }

        if (rating != null && rating !in 1..5) {
            return DataResourceResult.Failure(Exception("별점은 1점에서 5점 사이여야 합니다."))
        }

        val cleanedTags = tags.map { it.trim() }.filter { it.isNotBlank() }
        if (cleanedTags.size > 10) {
            return DataResourceResult.Failure(Exception("태그는 최대 10개까지만 등록 가능합니다."))
        }
        return postRepository.createPost(
            postId = postId,
            title = trimmedTitle,
            content = trimmedContent,
            imageUrls = imageUrls,
            teaType = teaType,
            rating = rating,
            tags = cleanedTags,
            brewingSummary = brewingSummary,
            originNoteId = originNoteId
        )
    }
}