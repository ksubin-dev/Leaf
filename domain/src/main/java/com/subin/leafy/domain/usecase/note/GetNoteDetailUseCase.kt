package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.repository.NoteRepository
import javax.inject.Inject

class GetNoteDetailUseCase @Inject constructor(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(noteId: String): DataResourceResult<BrewingNote> {
        if (noteId.isBlank()) {
            return DataResourceResult.Failure(Exception("잘못된 노트 ID입니다."))
        }
        return noteRepository.getNoteDetail(noteId)
    }
}