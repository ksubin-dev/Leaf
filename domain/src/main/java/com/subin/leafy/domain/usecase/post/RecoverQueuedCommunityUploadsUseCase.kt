package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.repository.PostRepository
import javax.inject.Inject

class RecoverQueuedCommunityUploadsUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(): Int {
        return repository.recoverQueuedCommunityUploads()
    }
}
