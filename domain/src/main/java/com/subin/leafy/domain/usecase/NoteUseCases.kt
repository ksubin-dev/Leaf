package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.note.*
import javax.inject.Inject

data class NoteUseCases @Inject constructor(
    val getMyNotes: GetMyNotesUseCase,
    val getNotesByMonth: GetNotesByMonthUseCase,
    val searchMyNotes: SearchMyNotesUseCase,
    val getNoteDetail: GetNoteDetailUseCase,
    val getUserNotes: GetUserNotesUseCase,

    val saveNote: SaveNoteUseCase,
    val updateNote: UpdateNoteUseCase,
    val deleteNote: DeleteNoteUseCase,

    val syncNotes: SyncNotesUseCase,
    val clearLocalCache: ClearLocalCacheUseCase,
    val scheduleNoteUpload: ScheduleNoteUpload
)