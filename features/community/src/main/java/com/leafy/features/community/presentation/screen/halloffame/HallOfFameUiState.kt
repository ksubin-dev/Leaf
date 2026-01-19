package com.leafy.features.community.presentation.screen.halloffame

import com.leafy.features.community.presentation.common.model.CommunityPostUiModel
import com.subin.leafy.domain.model.RankingPeriod

data class HallOfFameUiState(
    val isLoading: Boolean = true,
    val selectedPeriod: RankingPeriod = RankingPeriod.WEEKLY,
    val posts: List<CommunityPostUiModel> = emptyList(),
    val errorMessage: String? = null
)