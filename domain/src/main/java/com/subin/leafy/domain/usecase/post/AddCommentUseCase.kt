package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Comment
import com.subin.leafy.domain.repository.PostRepository
import javax.inject.Inject

class AddCommentUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    suspend operator fun invoke(postId: String, content: String): DataResourceResult<Unit> {

        if (postId.isBlank()) {
            return DataResourceResult.Failure(Exception("잘못된 게시글 ID입니다."))
        }

        val trimmedContent = content.trim()

        if (trimmedContent.isBlank()) {
            return DataResourceResult.Failure(Exception("댓글 내용을 입력해주세요."))
        }

        if (trimmedContent.length > 200) {
            return DataResourceResult.Failure(Exception("댓글은 200자 이내로 작성해주세요."))
        }

        return postRepository.addComment(postId, trimmedContent)
    }
}