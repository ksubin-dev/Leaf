package com.leafy.features.community.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.ExploreTab
import com.subin.leafy.domain.usecase.CommunityUseCases
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface CommunityUiEffect {
    data class ShowToast(val message: String) : CommunityUiEffect
}

class CommunityViewModel(
    private val communityUseCases: CommunityUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<CommunityUiEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        readAll()
    }

    fun readAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            launch { fetchPopularNotes() }
            launch { fetchRisingNotes() }
            launch { fetchMasters() }
            launch { fetchFollowingFeed() }
            launch { fetchPopularTags() }
            launch { fetchMostSavedNotes() }
        }
    }

    private suspend fun fetchPopularNotes() {
        communityUseCases.getPopularNotes().collectLatest { result ->
            handleDataResult(result) { data ->
                _uiState.update { it.copy(popularNotes = data.toSummaryUi()) }
            }
        }
    }

    private suspend fun fetchRisingNotes() {
        communityUseCases.getRisingNotes().collectLatest { result ->
            handleDataResult(result) { data ->
                _uiState.update { it.copy(risingNotes = data.toSummaryUi()) }
            }
        }
    }

    private suspend fun fetchPopularTags() {
        communityUseCases.getPopularTags().collectLatest { result ->
            handleDataResult(result) { data ->
                _uiState.update { it.copy(popularTags = data.toTagUi()) }
            }
        }
    }

    private suspend fun fetchMostSavedNotes() {
        communityUseCases.getMostSavedNotes().collectLatest { result ->
            handleDataResult(result) { data ->
                _uiState.update { it.copy(mostSavedNotes = data.toSummaryUi()) }
            }
        }
    }

    private suspend fun fetchMasters() {
        communityUseCases.getRecommendedMasters().collectLatest { result ->
            handleDataResult(result) { data ->
                _uiState.update { it.copy(teaMasters = data.toMasterUi()) }
            }
        }
    }

    private suspend fun fetchFollowingFeed() {
        communityUseCases.getFollowingFeed().collectLatest { result ->
            handleDataResult(result) { data ->
                _uiState.update { it.copy(followingFeed = data.toFollowingUi()) }
            }
        }
    }

    private fun <T> handleDataResult(
        result: DataResourceResult<T>,
        onSuccess: (T) -> Unit
    ) {
        when (result) {
            is DataResourceResult.Loading -> {
                _uiState.update { it.copy(isLoading = true) }
            }
            is DataResourceResult.Success -> {
                onSuccess(result.data)
                _uiState.update { it.copy(isLoading = false) }
            }
            is DataResourceResult.Failure -> {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = result.exception.message)
                }
            }
            else -> Unit
        }
    }

    fun onTabSelected(tab: ExploreTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            val result = communityUseCases.toggleLike(postId)
            if (result is DataResourceResult.Success) {
                _effect.send(CommunityUiEffect.ShowToast("좋아요가 반영되었습니다."))
            } else if (result is DataResourceResult.Failure) {
                _effect.send(CommunityUiEffect.ShowToast("오류 발생: ${result.exception.message}"))
            }
        }
    }
}