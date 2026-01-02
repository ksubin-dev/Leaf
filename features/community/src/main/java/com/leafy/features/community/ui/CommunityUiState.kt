package com.leafy.features.community.ui

import com.leafy.features.community.ui.component.ExploreNoteUi
import com.leafy.features.community.ui.component.ExploreTagUi
import com.leafy.features.community.ui.component.ExploreTeaMasterUi
import com.subin.leafy.domain.model.ExploreTab

data class CommunityUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: ExploreTab = ExploreTab.TRENDING,

    val popularNotes: List<ExploreNoteUi> = emptyList(),
    val risingNotes: List<ExploreNoteUi> = emptyList(),
    val mostSavedNotes: List<ExploreNoteUi> = emptyList(),
    val followingFeed: List<ExploreNoteUi> = emptyList(),
    val teaMasters: List<ExploreTeaMasterUi> = emptyList(),
    val popularTags: List<ExploreTagUi> = emptyList()
)