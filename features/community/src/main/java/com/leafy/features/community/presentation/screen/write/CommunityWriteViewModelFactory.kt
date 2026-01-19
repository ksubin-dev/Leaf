package com.leafy.features.community.presentation.screen.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases

class CommunityWriteViewModelFactory(
    private val postUseCases: PostUseCases,
    private val noteUseCases: NoteUseCases,
    private val userUseCases: UserUseCases,
    private val imageUseCases: ImageUseCases,
    private val imageCompressor: ImageCompressor
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunityWriteViewModel::class.java)) {
            return CommunityWriteViewModel(
                postUseCases = postUseCases,
                noteUseCases = noteUseCases,
                userUseCases = userUseCases,
                imageUseCases = imageUseCases,
                imageCompressor = imageCompressor
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}