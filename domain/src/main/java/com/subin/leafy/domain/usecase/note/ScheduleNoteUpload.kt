package com.subin.leafy.domain.usecase.note

import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.repository.NoteRepository
import javax.inject.Inject

class ScheduleNoteUpload @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: BrewingNote, imageUriStrings: List<String>, isEditMode: Boolean) {
        repository.scheduleNoteUpload(note, imageUriStrings, isEditMode)
    }
}