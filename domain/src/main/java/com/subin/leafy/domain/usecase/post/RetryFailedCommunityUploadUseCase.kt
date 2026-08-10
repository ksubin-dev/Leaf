package com.subin.leafy.domain.usecase.post

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.PostRepository
import javax.inject.Inject

class RetryFailedCommunityUploadUseCase @Inject constructor(
    private val repository: PostRepository
) {
    suspend operator fun invoke(queueId: String): DataResourceResult<Unit> {
        return repository.retryFailedCommunityUpload(queueId)
    }
}
