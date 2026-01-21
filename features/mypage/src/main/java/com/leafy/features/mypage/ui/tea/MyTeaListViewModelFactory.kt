package com.leafy.features.mypage.ui.tea

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.leafy.shared.di.ApplicationContainer

@Composable
fun makeMyTeaListViewModelFactory(container: ApplicationContainer): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MyTeaListViewModel(
                teaUseCases = container.teaUseCases
            ) as T
        }
    }
}