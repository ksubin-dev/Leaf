package com.leafy.shared.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface MainNavigationRoute : LeafyNavigation {

    @Serializable data object Auth : MainNavigationRoute
    @Serializable data object HomeTab : MainNavigationRoute
    @Serializable
    data class NoteTab(
        val initialRecords: String? = null,
        val noteId: String? = null,
        val date: String? = null
    ) : MainNavigationRoute

    @Serializable
    data class NoteDetail(val noteId: String) : MainNavigationRoute
    @Serializable data object CommunityTab : MainNavigationRoute
    @Serializable data class CommunityDetail(val postId: String) : MainNavigationRoute
    @Serializable data object CommunityWrite : MainNavigationRoute
    @Serializable data class MasterProfile(val userId: String) : MainNavigationRoute
    @Serializable data object TimerTab : MainNavigationRoute
    @Serializable data object MyPageTab : MainNavigationRoute
    @Serializable data class DailyRecords(val date: String) : MainNavigationRoute

    @Serializable data object AnalysisReport : MainNavigationRoute
}