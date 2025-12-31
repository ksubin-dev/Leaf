package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNoteByIdUseCase(private val repository: NoteRepository) {
    operator fun invoke(userId: String, noteId: String): Flow<DataResourceResult<BrewingNote>> {
        return repository.getNoteById(userId, noteId)
    }
}