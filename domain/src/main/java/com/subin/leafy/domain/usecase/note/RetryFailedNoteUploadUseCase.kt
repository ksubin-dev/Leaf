package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.NoteRepository
import javax.inject.Inject

class RetryFailedNoteUploadUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(queueId: String): DataResourceResult<Unit> {
        return repository.retryFailedNoteUpload(queueId)
    }
}
