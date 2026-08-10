package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.repository.NoteRepository
import javax.inject.Inject

class RecoverQueuedNoteUploadsUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(): Int {
        return repository.recoverQueuedNoteUploads()
    }
}
