package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class InsertNoteUseCase(private val repository: NoteRepository) {
    operator fun invoke(note: BrewingNote): Flow<DataResourceResult<Unit>> {
        return repository.create(note)
    }
}