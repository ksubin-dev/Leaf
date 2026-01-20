package com.leafy.features.community.presentation.screen.popular

import com.leafy.shared.ui.model.CommunityPostUiModel

data class PopularPostListUiState(
    val isLoading: Boolean = true,
    val posts: List<CommunityPostUiModel> = emptyList(),
    val errorMessage: String? = null
)
