package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.id.NoteId
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class DeleteNoteUseCase(private val repository: NoteRepository) {
    operator fun invoke(noteId: NoteId): Flow<DataResourceResult<Unit>> {
        return repository.delete(noteId)
    }
}