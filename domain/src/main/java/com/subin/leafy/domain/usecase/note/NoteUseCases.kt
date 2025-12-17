package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.usecase.note.DeleteNoteUseCase
import com.subin.leafy.domain.usecase.note.GetNotesUseCase
import com.subin.leafy.domain.usecase.note.InsertNoteUseCase
import com.subin.leafy.domain.usecase.note.UpdateNoteUseCase

data class NoteUseCases(
    val getNotes: GetNotesUseCase,
    val insertNote: InsertNoteUseCase,
    val updateNote: UpdateNoteUseCase,
    val deleteNote: DeleteNoteUseCase
)