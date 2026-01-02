package com.leafy.features.note.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leafy.features.note.ui.NoteDetailViewModel
import com.subin.leafy.domain.usecase.NoteUseCases

class NoteDetailViewModelFactory(
    private val noteUseCases: NoteUseCases,
    private val noteId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteDetailViewModel::class.java)) {
            return NoteDetailViewModel(noteUseCases, noteId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}