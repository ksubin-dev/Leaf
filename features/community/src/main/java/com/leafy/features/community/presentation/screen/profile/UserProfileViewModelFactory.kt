package com.leafy.features.community.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases

class UserProfileViewModelFactory {
    companion object {
        fun provide(
            targetUserId: String,
            userUseCases: UserUseCases,
            postUseCases: PostUseCases
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserProfileViewModel(
                    targetUserId,
                    userUseCases,
                    postUseCases
                ) as T
            }
        }
    }
}