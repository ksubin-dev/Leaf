package com.leafy.features.home.presentation.ranking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.leafy.features.home.presentation.home.RankingFilter
import com.leafy.shared.R
import com.leafy.shared.navigation.MainNavigationRoute
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.RankingItem
import com.subin.leafy.domain.usecase.PostUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class RankingDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postUseCases: PostUseCases
) : ViewModel() {

    private val route = savedStateHandle.toRoute<MainNavigationRoute.RankingDetail>()

    private val initialFilter = RankingFilter.entries.find { it.name == route.initialFilterName }
        ?: RankingFilter.THIS_WEEK

    private val _uiState = MutableStateFlow(RankingDetailUiState(selectedFilter = initialFilter))
    val uiState = _uiState.asStateFlow()

    private val _selectedFilter = MutableStateFlow(initialFilter)

    private val _sideEffect = Channel<RankingSideEffect>()
    val sideEffect: Flow<RankingSideEffect> = _sideEffect.receiveAsFlow()

    init {
        observeRanking()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeRanking() {
        _selectedFilter
            .flatMapLatest { filter ->
                flow {
                    _uiState.update { it.copy(isLoading = true, selectedFilter = filter) }
                    emitAll(postUseCases.getWeeklyRanking(filter.teaType))
                }
            }
            .onEach { result ->
                when (result) {
                    is DataResourceResult.Success -> {
                        _uiState.update {
                            it.copy(rankingList = result.data, isLoading = false)
                        }
                    }
                    is DataResourceResult.Failure -> {
                        _uiState.update {
                            it.copy(rankingList = emptyList(), isLoading = false)
                        }
                        _sideEffect.send(RankingSideEffect.ShowToast(
                            UiText.StringResource(R.string.msg_ranking_load_failed)
                        ))
                    }
                    else -> {}
                }
            }
            .launchIn(viewModelScope)
    }

    fun onFilterSelected(filter: RankingFilter) {
        if (_selectedFilter.value == filter) return
        _selectedFilter.value = filter
    }
}

data class RankingDetailUiState(
    val isLoading: Boolean = false,
    val selectedFilter: RankingFilter = RankingFilter.THIS_WEEK,
    val rankingList: List<RankingItem> = emptyList()
)

sealed interface RankingSideEffect {
    data class ShowToast(val message: UiText) : RankingSideEffect
}