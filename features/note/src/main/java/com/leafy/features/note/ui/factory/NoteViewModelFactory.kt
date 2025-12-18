package com.leafy.features.note.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leafy.features.note.ui.NoteViewModel
import com.subin.leafy.domain.model.InfusionRecord
import com.subin.leafy.domain.usecase.note.NoteUseCases

class NoteViewModelFactory(
    private val noteUseCases: NoteUseCases,
    private val initialRecords: List<InfusionRecord>? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(noteUseCases, initialRecords) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}