package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetNotesByMonthUseCase(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(year: Int, month: Int): Flow<List<BrewingNote>> {
        if (year < 2000 || month !in 1..12) {
            return flowOf(emptyList())
        }
        return noteRepository.getNotesByMonthFlow( year, month)
    }
}