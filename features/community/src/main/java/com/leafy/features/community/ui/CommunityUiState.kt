package com.leafy.features.community.ui

import com.leafy.features.community.ui.component.ExploreFollowingNoteUi
import com.leafy.features.community.ui.component.ExploreNoteSummaryUi
import com.leafy.features.community.ui.component.ExploreTagUi
import com.leafy.features.community.ui.component.ExploreTeaMasterUi
import com.subin.leafy.domain.model.ExploreTab


data class CommunityUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: ExploreTab = ExploreTab.TRENDING,

    // 도메인 모델 대신 UI 전용 모델 리스트를 보유합니다.
    val popularNotes: List<ExploreNoteSummaryUi> = emptyList(),
    val risingNotes: List<ExploreNoteSummaryUi> = emptyList(),
    val mostSavedNotes: List<ExploreNoteSummaryUi> = emptyList(),
    val teaMasters: List<ExploreTeaMasterUi> = emptyList(),
    val popularTags: List<ExploreTagUi> = emptyList(),
    val followingFeed: List<ExploreFollowingNoteUi> = emptyList()
)