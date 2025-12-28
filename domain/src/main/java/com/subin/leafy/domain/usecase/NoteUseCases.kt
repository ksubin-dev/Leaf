package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.note.DeleteNoteUseCase
import com.subin.leafy.domain.usecase.note.GetMonthlyRecordsUseCase
import com.subin.leafy.domain.usecase.note.GetNoteByIdUseCase
import com.subin.leafy.domain.usecase.note.GetNotesUseCase
import com.subin.leafy.domain.usecase.note.GetRecordByDateUseCase
import com.subin.leafy.domain.usecase.note.InsertNoteUseCase
import com.subin.leafy.domain.usecase.note.UpdateNoteUseCase
import com.subin.leafy.domain.usecase.user.GetCurrentUserIdUseCase

data class NoteUseCases(
    val getNotes: GetNotesUseCase,
    val getNoteById: GetNoteByIdUseCase,
    val insertNote: InsertNoteUseCase,
    val updateNote: UpdateNoteUseCase,
    val deleteNote: DeleteNoteUseCase,
    val getCurrentUserId: GetCurrentUserIdUseCase,
    val getMonthlyRecords: GetMonthlyRecordsUseCase,
    val getRecordByDate: GetRecordByDateUseCase
)