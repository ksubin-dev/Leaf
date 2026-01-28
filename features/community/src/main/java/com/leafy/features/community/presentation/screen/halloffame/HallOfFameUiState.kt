package com.leafy.features.community.presentation.screen.halloffame

import com.leafy.shared.ui.model.CommunityPostUiModel
import com.subin.leafy.domain.model.RankingPeriod

data class HallOfFameUiState(
    val isLoading: Boolean = false,
    val selectedPeriod: RankingPeriod = RankingPeriod.WEEKLY,
    val posts: List<CommunityPostUiModel> = emptyList()
)