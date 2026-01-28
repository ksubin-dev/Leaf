package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.repository.PostChangeEvent
import com.subin.leafy.domain.repository.PostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePostChangesUseCase @Inject constructor(
    private val postRepository: PostRepository
) {
    operator fun invoke(): Flow<PostChangeEvent> {
        return postRepository.postChangeFlow
    }
}