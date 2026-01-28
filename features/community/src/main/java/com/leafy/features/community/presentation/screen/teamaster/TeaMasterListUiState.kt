package com.leafy.features.community.presentation.screen.teamaster

import com.leafy.shared.ui.model.UserUiModel

data class TeaMasterListUiState(
    val isLoading: Boolean = true,
    val masters: List<UserUiModel> = emptyList(),
    val currentUserId: String? = null,
)