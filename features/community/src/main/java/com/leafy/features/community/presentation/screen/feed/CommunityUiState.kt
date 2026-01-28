package com.leafy.features.community.presentation.screen.feed

import com.leafy.shared.ui.model.CommentUiModel
import com.leafy.shared.ui.model.CommunityPostUiModel
import com.leafy.shared.ui.model.UserUiModel
import com.leafy.features.community.presentation.components.bar.CommunityTab

data class CommunityUiState(

    val selectedTab: CommunityTab = CommunityTab.TRENDING,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,


    val popularPosts: List<CommunityPostUiModel> = emptyList(),
    val mostBookmarkedPosts: List<CommunityPostUiModel> = emptyList(),
    val teaMasters: List<UserUiModel> = emptyList(),
    val followingPosts: List<CommunityPostUiModel> = emptyList(),
    val isFollowingEmpty: Boolean = false,
    val currentUserId: String? = null,
    val currentUserProfileUrl: String? = null,
    val showCommentSheet: Boolean = false,
    val selectedPostId: String? = null,
    val commentInput: String = "",
    val comments: List<CommentUiModel> = emptyList(),
    val isCommentLoading: Boolean = false

)