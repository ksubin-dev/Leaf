package com.leafy.features.home.viewmodel

import com.subin.leafy.domain.model.HomeBanner
import com.subin.leafy.domain.model.QuickBrewingGuide
import com.subin.leafy.domain.model.RankingItem
import com.subin.leafy.domain.model.TeaType


enum class RankingFilter(val label: String, val teaType: TeaType?) {
    THIS_WEEK("이번 주", null),

    GREEN(TeaType.GREEN.label, TeaType.GREEN),
    MATCHA(TeaType.MATCHA.label, TeaType.MATCHA),
    BLACK(TeaType.BLACK.label, TeaType.BLACK),
    OOLONG(TeaType.OOLONG.label, TeaType.OOLONG),
    WHITE(TeaType.WHITE.label, TeaType.WHITE),
    YELLOW(TeaType.YELLOW.label, TeaType.YELLOW),
    PUERH(TeaType.PUERH.label, TeaType.PUERH),
    HERBAL(TeaType.HERBAL.label, TeaType.HERBAL),
    ETC(TeaType.ETC.label, TeaType.ETC);
}

data class HomeUiState(
    val currentUserId: String? = null,
    val isLoading: Boolean = true,
    val userProfileUrl: String? = null,
    val banner: HomeBanner? = null,
    val quickGuide: QuickBrewingGuide? = null,
    val selectedFilter: RankingFilter = RankingFilter.THIS_WEEK,
    val rankingList: List<RankingItem> = emptyList(),
    val isRankingLoading: Boolean = false
)