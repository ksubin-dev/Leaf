package com.leafy.shared.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface MainNavigationRoute : LeafyNavigation {

    @Serializable data object Auth : MainNavigationRoute
    @Serializable data object HomeTab : MainNavigationRoute
    @Serializable data object Search : MainNavigationRoute
    @Serializable data object Notification : MainNavigationRoute
    @Serializable
    data class RankingDetail(
        val initialFilterLabel: String = "이번 주"
    ) : MainNavigationRoute
    @Serializable
    data class NoteTab(
        val initialRecords: String? = null,
        val noteId: String? = null,
        val date: String? = null
    ) : MainNavigationRoute

    @Serializable data class NoteDetail(val noteId: String) : MainNavigationRoute
    @Serializable data class UserProfile(val userId: String) : MainNavigationRoute

    @Serializable
    data class UserList(
        val userId: String,
        val nickname: String,
        val type: UserListType
    ) : MainNavigationRoute

    @Serializable data object CommunityTab : MainNavigationRoute
    @Serializable data class CommunityDetail(val postId: String, val autoFocus: Boolean = false) : MainNavigationRoute

    @Serializable data object CommunityWrite : MainNavigationRoute
    @Serializable data object PopularPostList : MainNavigationRoute

    @Serializable data object TeaMasterList : MainNavigationRoute
    @Serializable data object HallOfFameList : MainNavigationRoute
    @Serializable data object TimerTab : MainNavigationRoute
    @Serializable data object TimerPresetList : MainNavigationRoute
    @Serializable data object MyPageTab : MainNavigationRoute
    @Serializable data class DailyRecords(val date: String) : MainNavigationRoute

    @Serializable data object BookmarkedPosts : MainNavigationRoute
    @Serializable data object LikedPosts : MainNavigationRoute

    @Serializable data object FollowerList : MainNavigationRoute

    @Serializable data object FollowingList : MainNavigationRoute
    @Serializable data object Settings : MainNavigationRoute

    @Serializable data object MyTeaCabinet : MainNavigationRoute

    @Serializable data class TeaAddEdit(val teaId: String? = null) : MainNavigationRoute

    @Serializable data object AnalysisReport : MainNavigationRoute
}

enum class UserListType {
    FOLLOWER, FOLLOWING
}