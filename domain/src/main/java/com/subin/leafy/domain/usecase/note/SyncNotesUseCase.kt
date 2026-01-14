package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.NoteRepository

class SyncNotesUseCase(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(): DataResourceResult<Unit> {
        return noteRepository.syncNotes()
    }
}