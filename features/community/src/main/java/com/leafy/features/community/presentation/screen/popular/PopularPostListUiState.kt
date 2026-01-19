package com.leafy.features.community.presentation.screen.popular

import com.leafy.features.community.presentation.common.model.CommunityPostUiModel

data class PopularPostListUiState(
    val isLoading: Boolean = true,
    val posts: List<CommunityPostUiModel> = emptyList(),
    val errorMessage: String? = null
)
