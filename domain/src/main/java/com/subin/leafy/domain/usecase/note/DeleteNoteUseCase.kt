package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DeleteNoteUseCase(private val repository: NoteRepository) {

    operator fun invoke(currentUserId: String, note: BrewingNote): Flow<DataResourceResult<Unit>> {
        if (note.ownerId != currentUserId) {
            return flowOf(DataResourceResult.Failure(Exception("본인의 게시물만 삭제할 수 있습니다.")))
        }

        return repository.delete(note.id)
    }
}