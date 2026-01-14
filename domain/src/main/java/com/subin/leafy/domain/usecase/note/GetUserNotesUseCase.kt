package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.repository.NoteRepository

//타인의 노트 조회
class GetUserNotesUseCase(
    private val noteRepository: NoteRepository
) {
    suspend operator fun invoke(userId: String): DataResourceResult<List<BrewingNote>> {
        if (userId.isBlank()) {
            return DataResourceResult.Failure(Exception("유효하지 않은 유저 ID입니다."))
        }
        return noteRepository.getUserNotes(userId)
    }
}