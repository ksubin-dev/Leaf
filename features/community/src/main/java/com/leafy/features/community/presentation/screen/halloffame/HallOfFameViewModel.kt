package com.leafy.features.community.presentation.screen.halloffame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.R
import com.leafy.shared.ui.mapper.toUiModel
import com.leafy.shared.ui.model.CommunityPostUiModel
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.RankingPeriod
import com.subin.leafy.domain.usecase.PostUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HallOfFameSideEffect {
    data class ShowToast(val message: UiText) : HallOfFameSideEffect
}

@HiltViewModel
class HallOfFameViewModel @Inject constructor(
    private val postUseCases: PostUseCases
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(RankingPeriod.WEEKLY)

    private val _posts = MutableStateFlow<List<CommunityPostUiModel>>(emptyList())
    private val _isLoading = MutableStateFlow(false)

    private val _sideEffect = Channel<HallOfFameSideEffect>()
    val sideEffect: Flow<HallOfFameSideEffect> = _sideEffect.receiveAsFlow()

    init {
        observePeriodChanges()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observePeriodChanges() {
        _selectedPeriod
            .flatMapLatest { period ->
                flow {
                    _isLoading.value = true
                    emitAll(postUseCases.getMostBookmarkedPosts(period = period, limit = 50))
                }
            }
            .onEach { result ->
                _isLoading.value = false
                when (result) {
                    is DataResourceResult.Success -> {
                        _posts.value = result.data.map { it.toUiModel() }
                    }
                    is DataResourceResult.Failure -> {
                        _posts.value = emptyList()
                        sendEffect(HallOfFameSideEffect.ShowToast(
                            UiText.StringResource(R.string.msg_data_load_error)
                        ))
                    }
                    else -> {}
                }
            }
            .launchIn(viewModelScope)
    }

    val uiState: StateFlow<HallOfFameUiState> = combine(
        _selectedPeriod, _posts, _isLoading
    ) { period, posts, isLoading ->
        HallOfFameUiState(
            selectedPeriod = period,
            posts = posts,
            isLoading = isLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HallOfFameUiState(isLoading = true)
    )

    fun updatePeriod(period: RankingPeriod) {
        if (_selectedPeriod.value == period) return
        _selectedPeriod.value = period
    }

    fun toggleBookmark(post: CommunityPostUiModel) {
        val postId = post.postId
        val currentBookmarked = post.isBookmarked
        val newBookmarked = !currentBookmarked

        updatePostState(postId, newBookmarked)

        viewModelScope.launch {
            val result = postUseCases.toggleBookmark(postId)
            if (result is DataResourceResult.Failure) {
                updatePostState(postId, currentBookmarked)
                sendEffect(HallOfFameSideEffect.ShowToast(
                    UiText.StringResource(R.string.msg_bookmark_failed)
                ))
            } else if (newBookmarked) {
                sendEffect(HallOfFameSideEffect.ShowToast(
                    UiText.StringResource(R.string.msg_bookmark_saved)
                ))
            }
        }
    }

    private fun updatePostState(postId: String, isBookmarked: Boolean) {
        _posts.update { currentList ->
            currentList.map { item ->
                if (item.postId == postId) {
                    val currentCount = item.bookmarkCount.toIntOrNull() ?: 0
                    val newCount = (if (isBookmarked) currentCount + 1 else maxOf(0, currentCount - 1)).toString()
                    item.updateBookmarkState(isBookmarked, newCount)
                } else {
                    item
                }
            }
        }
    }

    private fun sendEffect(effect: HallOfFameSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }
}

private fun CommunityPostUiModel.updateBookmarkState(
    isBookmarked: Boolean,
    newCount: String
): CommunityPostUiModel = when (this) {
    is CommunityPostUiModel.General -> this.copy(
        isBookmarked = isBookmarked,
        bookmarkCount = newCount
    )
    is CommunityPostUiModel.BrewingNote -> this.copy(
        isBookmarked = isBookmarked,
        bookmarkCount = newCount
    )
}