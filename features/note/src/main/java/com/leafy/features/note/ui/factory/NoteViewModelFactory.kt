package com.leafy.features.note.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leafy.features.note.ui.NoteDetailViewModel
import com.leafy.features.note.ui.NoteViewModel
import com.subin.leafy.domain.model.InfusionRecord
import com.subin.leafy.domain.usecase.NoteUseCases

class NoteViewModelFactory(
    private val noteUseCases: NoteUseCases,
    private val initialRecords: List<InfusionRecord>? = null,
    private val noteId: String? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NoteViewModel::class.java) -> {
                NoteViewModel(noteUseCases, initialRecords) as T
            }

            modelClass.isAssignableFrom(NoteDetailViewModel::class.java) -> {
                NoteDetailViewModel(noteUseCases, noteId ?: "") as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}