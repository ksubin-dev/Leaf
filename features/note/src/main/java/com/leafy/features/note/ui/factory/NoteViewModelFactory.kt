package com.leafy.features.note.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leafy.features.note.ui.NoteDetailViewModel
import com.leafy.features.note.ui.NoteViewModel
import com.subin.leafy.domain.model.InfusionRecord
import com.subin.leafy.domain.usecase.CommunityUseCases
import com.subin.leafy.domain.usecase.NoteUseCases

class NoteViewModelFactory(
    private val noteUseCases: NoteUseCases,
    private val communityUseCases: CommunityUseCases,
    private val initialRecords: List<InfusionRecord>? = null,
    private val noteId: String? = null,
    private val selectedDate: String? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NoteViewModel::class.java) -> {
                NoteViewModel(noteUseCases, initialRecords, noteId, selectedDate) as T
            }
            modelClass.isAssignableFrom(NoteDetailViewModel::class.java) -> {
                NoteDetailViewModel(
                    noteUseCases = noteUseCases,
                    communityUseCases = communityUseCases,
                    noteId = noteId ?: throw IllegalArgumentException("noteId is required")
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}