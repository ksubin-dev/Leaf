package com.leafy.features.home.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.R
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.HomeUseCases
import com.subin.leafy.domain.usecase.NotificationUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeSideEffect {
    data class ShowToast(val message: UiText) : HomeSideEffect
    data class NavigateTo(val route: String) : HomeSideEffect
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeUseCases: HomeUseCases,
    private val postUseCases: PostUseCases,
    private val userUseCases: UserUseCases,
    private val notificationUseCases: NotificationUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val _selectedFilter = MutableStateFlow(RankingFilter.THIS_WEEK)

    private val _sideEffect = Channel<HomeSideEffect>()
    val sideEffect: Flow<HomeSideEffect> = _sideEffect.receiveAsFlow()

    init {
        loadUserProfile()
        loadStaticContents()
        observeNotifications()
        observeRanking()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            val myIdResult = userUseCases.getCurrentUserId()
            if (myIdResult is DataResourceResult.Success) {
                _uiState.update { it.copy(currentUserId = myIdResult.data) }
            }
        }

        userUseCases.getMyProfile().onEach { result ->
            if (result is DataResourceResult.Success) {
                _uiState.update { state ->
                    state.copy(
                        userProfileUrl = result.data.profileImageUrl,
                        currentUserId = result.data.id.ifBlank { state.currentUserId }
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeRanking() {
        _selectedFilter
            .flatMapLatest { filter ->
                flow {
                    _uiState.update { it.copy(isRankingLoading = true, selectedFilter = filter) }
                    emitAll(postUseCases.getWeeklyRanking(filter.teaType))
                }
            }
            .onEach { result ->
                when (result) {
                    is DataResourceResult.Success -> {
                        _uiState.update {
                            it.copy(rankingList = result.data, isRankingLoading = false)
                        }
                    }
                    is DataResourceResult.Failure -> {
                        Log.e("HomeViewModel", "랭킹 로드 실패: ${result.exception.message}")
                        _uiState.update {
                            it.copy(rankingList = emptyList(), isRankingLoading = false)
                        }
                        sendEffect(HomeSideEffect.ShowToast(UiText.StringResource(R.string.msg_ranking_load_failed)))
                    }
                    else -> {}
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeNotifications() {
        notificationUseCases.getNotifications()
            .onEach { result ->
                if (result is DataResourceResult.Success) {
                    val hasUnread = result.data.any { !it.isRead }
                    _uiState.update { it.copy(hasUnreadNotifications = hasUnread) }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadStaticContents() {
        viewModelScope.launch {
            when (val result = homeUseCases.getHomeContent()) {
                is DataResourceResult.Success -> {
                    _uiState.update {
                        it.copy(
                            banner = result.data.banners.firstOrNull(),
                            quickGuide = result.data.quickGuide,
                            isLoading = false
                        )
                    }
                }
                is DataResourceResult.Failure -> {
                    Log.e("HomeViewModel", "홈 컨텐츠 로드 실패: ${result.exception.message}")
                    _uiState.update { it.copy(isLoading = false) }
                    sendEffect(HomeSideEffect.ShowToast(UiText.StringResource(R.string.msg_home_content_load_failed)))
                }
                else -> {}
            }
        }
    }

    fun onRankingFilterSelected(filter: RankingFilter) {
        if (_selectedFilter.value == filter) return
        _selectedFilter.value = filter
    }

    fun refreshNotificationState() {
    }

    private fun sendEffect(effect: HomeSideEffect) {
        viewModelScope.launch {
            _sideEffect.send(effect)
        }
    }
}