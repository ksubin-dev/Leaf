package com.subin.leafy.domain.usecase

import com.subin.leafy.domain.usecase.note.*

data class NoteUseCases(
    // 조회
    val getMyNotes: GetMyNotesUseCase,
    val getNotesByMonth: GetNotesByMonthUseCase,
    val searchMyNotes: SearchMyNotesUseCase,
    val getNoteDetail: GetNoteDetailUseCase,
    val getUserNotes: GetUserNotesUseCase,

    // CRUD
    val saveNote: SaveNoteUseCase,
    val updateNote: UpdateNoteUseCase,
    val deleteNote: DeleteNoteUseCase,

    // 동기화
    val syncNotes: SyncNotesUseCase,
    val clearLocalCache: ClearLocalCacheUseCase
)