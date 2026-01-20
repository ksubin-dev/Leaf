package com.leafy.features.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.HomeUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeUseCases: HomeUseCases,
    private val postUseCases: PostUseCases,
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private var rankingJob: Job? = null

    init {
        loadAllData()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            loadUserProfile()
            fetchRanking(RankingFilter.THIS_WEEK)
            loadStaticContents()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadUserProfile() {
        val myIdResult = userUseCases.getCurrentUserId()
        if (myIdResult is DataResourceResult.Success) {
            val myId = myIdResult.data
            _uiState.update { it.copy(currentUserId = myId) }
        }

        userUseCases.getMyProfile().onEach { result ->
            if (result is DataResourceResult.Success) {
                _uiState.update {
                    it.copy(
                        userProfileUrl = result.data.profileImageUrl,
                        currentUserId = result.data.id.ifBlank { it.currentUserId }
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
    private suspend fun loadStaticContents() {
        val result = homeUseCases.getHomeContent()
        when (result) {
            is DataResourceResult.Success -> {
                val content = result.data
                _uiState.update {
                    it.copy(
                        banner = content.banners.firstOrNull(),
                        quickGuide = content.quickGuide
                    )
                }
            }
            is DataResourceResult.Failure -> {
                Log.e("HomeViewModel", "홈 컨텐츠 로드 실패: ${result.exception.message}", result.exception)
            }

            else -> {}
        }
    }

    fun onRankingFilterSelected(filter: RankingFilter) {
        if (_uiState.value.selectedFilter == filter) return

        _uiState.update { it.copy(selectedFilter = filter, isRankingLoading = true) }
        fetchRanking(filter)
    }

    private fun fetchRanking(filter: RankingFilter) {
        rankingJob?.cancel()

        rankingJob = postUseCases.getWeeklyRanking(filter.teaType)
            .onEach { result ->
                when (result) {
                    is DataResourceResult.Success -> {
                        _uiState.update {
                            it.copy(rankingList = result.data, isRankingLoading = false)
                        }
                    }
                    is DataResourceResult.Failure -> {
                        Log.e("HomeViewModel", "랭킹 로드 실패: ${result.exception.message}", result.exception)
                        _uiState.update {
                            it.copy(rankingList = emptyList(), isRankingLoading = false)
                        }
                    }

                    else -> {}
                }
            }
            .launchIn(viewModelScope)
    }
}