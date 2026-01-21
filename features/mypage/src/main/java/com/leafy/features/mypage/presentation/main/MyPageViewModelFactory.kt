package com.leafy.features.mypage.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leafy.features.mypage.presentation.main.MyPageViewModel
import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.domain.usecase.AnalysisUseCases
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.TeaUseCases
import com.subin.leafy.domain.usecase.UserUseCases

class MyPageViewModelFactory(
    private val userUseCases: UserUseCases,
    private val noteUseCases: NoteUseCases,
    private val postUseCases: PostUseCases,
    private val analysisUseCases: AnalysisUseCases,
    private val imageUseCases: ImageUseCases,
    private val teaUseCases: TeaUseCases,
    private val imageCompressor: ImageCompressor
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyPageViewModel(
            userUseCases,
            noteUseCases,
            postUseCases,
            analysisUseCases,
            imageUseCases,
            teaUseCases,
            imageCompressor
        ) as T
    }
}