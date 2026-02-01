package com.leafy.features.search

import androidx.annotation.StringRes
import com.leafy.shared.R
import com.leafy.shared.ui.model.CommunityPostUiModel
import com.leafy.shared.ui.model.UserUiModel

enum class SearchTab(@StringRes val labelResId: Int) {
    POSTS(R.string.tab_search_posts),
    USERS(R.string.tab_search_users)
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