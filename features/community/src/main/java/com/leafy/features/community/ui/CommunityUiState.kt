package com.leafy.features.community.ui

import com.leafy.features.community.ui.component.ExploreNoteUi
import com.leafy.features.community.ui.component.ExploreTeaMasterUi
import com.subin.leafy.domain.model.ExploreContent

data class CommunityUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedTab: ExploreContent = ExploreContent.TRENDING,

    // 1. 이번 주 인기 노트
    val popularNotes: List<ExploreNoteUi> = emptyList(),

    // 2. 가장 많이 저장된 노트
    val mostSavedNotes: List<ExploreNoteUi> = emptyList(),

    // 4. 이번 달 티 마스터 추천
    val teaMasters: List<ExploreTeaMasterUi> = emptyList(),

    // 5. 팔로잉 탭 전용 피드 데이터
    val followingFeed: List<ExploreNoteUi> = emptyList(),
    val comments: List<CommunityCommentUi> = emptyList(),
    val isCommentsLoading: Boolean = false,

    ) {
    val hasError: Boolean get() = errorMessage != null

    // 현재 선택된 탭이 추천(Trending)인지 확인
    val isTrendingTab: Boolean get() = selectedTab == ExploreContent.TRENDING
}