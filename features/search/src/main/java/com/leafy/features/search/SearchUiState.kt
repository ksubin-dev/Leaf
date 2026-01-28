package com.leafy.features.search

import com.leafy.shared.ui.model.CommunityPostUiModel
import com.leafy.shared.ui.model.UserUiModel

enum class SearchTab(val label: String) {
    POSTS("게시글"),
    USERS("사용자")
}

data class SearchUiState(
    val query: String = "",
    val selectedTab: SearchTab = SearchTab.POSTS,
    val postResults: List<CommunityPostUiModel> = emptyList(),
    val userResults: List<UserUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isLastPage: Boolean = false,
)