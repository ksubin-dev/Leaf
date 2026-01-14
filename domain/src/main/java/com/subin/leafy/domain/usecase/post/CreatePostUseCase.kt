package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.PostRepository

class CreatePostUseCase(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(
        postId: String,
        title: String,
        content: String,
        imageUrls: List<String>,
        teaType: String?,   // "GREEN", "BLACK" 등 (없으면 null)
        rating: Int?,       // 1~5 (없으면 null)
        tags: List<String>, // ["#힐링", "#홈카페"]
        brewingSummary: String?, // "95℃ · 3분"
        originNoteId: String? = null
    ): DataResourceResult<Unit> {

        // 이미지 최소 1장
        if (imageUrls.isEmpty()) {
            return DataResourceResult.Failure(Exception("게시글에는 최소 1장의 사진이 필요합니다."))
        }
        // 1. 제목 검사 (필수, 길이 제한)
        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) {
            return DataResourceResult.Failure(Exception("제목을 입력해주세요."))
        }
        if (trimmedTitle.length > 50) {
            return DataResourceResult.Failure(Exception("제목은 50자 이내로 작성해주세요."))
        }

        // 2. 내용 검사 (필수, 최소 길이)
        val trimmedContent = content.trim()
        if (trimmedContent.isBlank()) {
            return DataResourceResult.Failure(Exception("내용을 입력해주세요."))
        }
        if (trimmedContent.length < 10) {
            return DataResourceResult.Failure(Exception("내용은 최소 10자 이상 작성해주세요."))
        }

        // 3. 별점 검사 (범위 체크)
        if (rating != null && rating !in 1..5) {
            return DataResourceResult.Failure(Exception("별점은 1점에서 5점 사이여야 합니다."))
        }

        // 4. 태그 정제 (빈 태그 제거 & 개수 제한)
        val cleanedTags = tags.map { it.trim() }.filter { it.isNotBlank() }
        if (cleanedTags.size > 10) {
            return DataResourceResult.Failure(Exception("태그는 최대 10개까지만 등록 가능합니다."))
        }

        // 5. 모든 검사 통과 시 레포지토리 호출
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