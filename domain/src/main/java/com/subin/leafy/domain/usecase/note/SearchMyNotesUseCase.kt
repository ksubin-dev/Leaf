package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SearchMyNotesUseCase(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(query: String): Flow<List<BrewingNote>> {
        val trimmedQuery = query.trim()
        if (trimmedQuery.isBlank()) {
            return flowOf(emptyList())
        }
        return noteRepository.searchMyNotes(trimmedQuery)
    }
}