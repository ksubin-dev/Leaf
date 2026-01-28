package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.NoteRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(noteId: String): DataResourceResult<Unit> {
        if (noteId.isBlank()) {
            return DataResourceResult.Failure(Exception("삭제할 노트 ID가 없습니다."))
        }
        return noteRepository.deleteNote(noteId)
    }
}