package com.leafy.features.community.presentation.screen.feed

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases

object CommunityFeedViewModelFactory {
    fun provide(
        postUseCases: PostUseCases,
        userUseCases: UserUseCases
    ): ViewModelProvider.Factory = viewModelFactory {
        initializer {
            CommunityFeedViewModel(
                postUseCases = postUseCases,
                userUseCases = userUseCases
            )
        }
    }
}