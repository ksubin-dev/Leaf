package com.subin.leafy.di

import com.subin.leafy.domain.usecase.note.*
import com.leafy.shared.di.ApplicationContainer
import com.subin.leafy.data.repository.MockNoteRepositoryImpl

class ApplicationContainerImpl : ApplicationContainer {
    private val repository = MockNoteRepositoryImpl()

    override val noteUseCases = NoteUseCases(
        getNotes = GetNotesUseCase(repository),
        insertNote = InsertNoteUseCase(repository),
        updateNote = UpdateNoteUseCase(repository),
        deleteNote = DeleteNoteUseCase(repository)
    )
}