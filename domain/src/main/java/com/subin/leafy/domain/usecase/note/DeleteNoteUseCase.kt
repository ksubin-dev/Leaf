package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class DeleteNoteUseCase(private val repository: NoteRepository) {
    operator fun invoke(id : String): Flow<DataResourceResult<Unit>> {
        return repository.delete(id)
    }
}