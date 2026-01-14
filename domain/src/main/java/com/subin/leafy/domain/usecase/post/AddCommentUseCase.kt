package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Comment
import com.subin.leafy.domain.repository.PostRepository

class AddCommentUseCase(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String, comment: Comment): DataResourceResult<Unit> {

        // 1. 게시글 ID 검사
        if (postId.isBlank()) {
            return DataResourceResult.Failure(Exception("잘못된 게시글 ID입니다."))
        }

        // 2. 내용 유효성 검사
        val trimmedContent = comment.content.trim()

        if (trimmedContent.isBlank()) {
            return DataResourceResult.Failure(Exception("댓글 내용을 입력해주세요."))
        }

        // 길이 제한
        if (trimmedContent.length > 200) {
            return DataResourceResult.Failure(Exception("댓글은 200자 이내로 작성해주세요."))
        }

        // 3. 정제된 내용으로 업데이트된 객체 생성
        val validComment = comment.copy(content = trimmedContent)

        return postRepository.addComment(postId, validComment)
    }
}