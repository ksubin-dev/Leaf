package com.leafy.features.mypage.ui.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leafy.features.mypage.ui.MyPageViewModel
import com.subin.leafy.domain.usecase.AnalysisUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases

class MyPageViewModelFactory(
    private val userUseCases: UserUseCases,
    private val noteUseCases: NoteUseCases,
    private val postUseCases: PostUseCases,
    private val analysisUseCases: AnalysisUseCases
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            return MyPageViewModel(
                userUseCases = userUseCases,
                noteUseCases = noteUseCases,
                postUseCases = postUseCases,
                analysisUseCases = analysisUseCases
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}