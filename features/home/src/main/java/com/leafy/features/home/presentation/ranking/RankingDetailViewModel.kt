package com.leafy.features.home.presentation.ranking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.leafy.features.home.presentation.home.RankingFilter
import com.leafy.shared.navigation.MainNavigationRoute
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.RankingItem
import com.subin.leafy.domain.usecase.PostUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RankingDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val postUseCases: PostUseCases
) : ViewModel() {

    private val route = savedStateHandle.toRoute<MainNavigationRoute.RankingDetail>()

    private val _uiState = MutableStateFlow(RankingDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val initialFilter = RankingFilter.entries.find { it.label == route.initialFilterLabel }
            ?: RankingFilter.THIS_WEEK

        onFilterSelected(initialFilter)
    }

    fun onFilterSelected(filter: RankingFilter) {
        if (_uiState.value.selectedFilter == filter && _uiState.value.rankingList.isNotEmpty()) return

        _uiState.update { it.copy(selectedFilter = filter, isLoading = true) }

        viewModelScope.launch {
            postUseCases.getWeeklyRanking(filter.teaType).collectLatest { result ->
                if (result is DataResourceResult.Success) {
                    _uiState.update {
                        it.copy(rankingList = result.data, isLoading = false)
                    }
                } else {
                    _uiState.update {
                        it.copy(rankingList = emptyList(), isLoading = false)
                    }
                }
            }
        }
    }
}

data class RankingDetailUiState(
    val isLoading: Boolean = false,
    val selectedFilter: RankingFilter = RankingFilter.THIS_WEEK,
    val rankingList: List<RankingItem> = emptyList()
)