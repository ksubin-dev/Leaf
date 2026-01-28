package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Comment
import com.subin.leafy.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    operator fun invoke(postId: String): Flow<DataResourceResult<List<Comment>>> {
        if (postId.isBlank()) {
            return flowOf(DataResourceResult.Failure(Exception("잘못된 게시글 ID입니다.")))
        }
        return postRepository.getComments(postId)
    }
}