package com.leafy.features.mypage.ui.tea

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.leafy.shared.di.ApplicationContainer
import com.leafy.shared.utils.ImageCompressor

@Composable
private fun makeTeaAddEditViewModelFactory(container: ApplicationContainer): ViewModelProvider.Factory {
    val context = LocalContext.current
    return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val savedStateHandle = extras.createSavedStateHandle()

            return TeaAddEditViewModel(
                savedStateHandle = savedStateHandle,
                teaUseCases = container.teaUseCases,
                imageUseCases = container.imageUseCases,
                imageCompressor = ImageCompressor(context)
            ) as T
        }
    }
}