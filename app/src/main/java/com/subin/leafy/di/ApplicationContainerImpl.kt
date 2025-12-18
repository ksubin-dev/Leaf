package com.subin.leafy.di

import com.subin.leafy.domain.usecase.note.*
import com.leafy.shared.di.ApplicationContainer
import com.subin.leafy.data.repository.MockNoteRepositoryImpl
import com.subin.leafy.data.repository.MockTimerRepositoryImpl
import com.subin.leafy.domain.usecase.timer.GetPresetsUseCase
import com.subin.leafy.domain.usecase.timer.TimerUseCases

class ApplicationContainerImpl : ApplicationContainer {
    private val repository = MockNoteRepositoryImpl()
    private val timerRepository = MockTimerRepositoryImpl()

    override val noteUseCases = NoteUseCases(
        getNotes = GetNotesUseCase(repository),
        insertNote = InsertNoteUseCase(repository),
        updateNote = UpdateNoteUseCase(repository),
        deleteNote = DeleteNoteUseCase(repository)
    )

    override val timerUseCases = TimerUseCases(
        getPresets = GetPresetsUseCase(timerRepository)
    )
}