package com.leafy.features.community.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.subin.leafy.domain.usecase.CommunityUseCases

class CommunityViewModelFactory(
    private val communityUseCases: CommunityUseCases
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunityViewModel::class.java)) {
            return CommunityViewModel(communityUseCases) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}