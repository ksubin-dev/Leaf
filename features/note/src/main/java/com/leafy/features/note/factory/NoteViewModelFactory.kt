package com.leafy.features.note.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leafy.features.note.viewmodel.DetailViewModel
import com.leafy.features.note.viewmodel.NoteViewModel
import com.leafy.shared.util.ImageCompressor
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases

class NoteViewModelFactory(
    private val noteUseCases: NoteUseCases,
    private val userUseCases: UserUseCases,
    private val imageUseCases: ImageUseCases,
    private val postUseCases: PostUseCases,
    private val imageCompressor: ImageCompressor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NoteViewModel::class.java) -> {
                NoteViewModel(
                    noteUseCases = noteUseCases,
                    userUseCases = userUseCases,
                    imageUseCases = imageUseCases,
                    imageCompressor = imageCompressor
                ) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(
                    noteUseCases = noteUseCases,
                    userUseCases = userUseCases,
                    postUseCases = postUseCases
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}