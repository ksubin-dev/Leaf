package com.leafy.features.community.presentation.screen.halloffame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.ui.mapper.toUiModel
import com.leafy.shared.ui.model.CommunityPostUiModel
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.RankingPeriod
import com.subin.leafy.domain.usecase.PostUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HallOfFameViewModel(
    private val postUseCases: PostUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(HallOfFameUiState())
    val uiState = _uiState.asStateFlow()

    private var fetchJob: Job? = null

    init {
        loadPosts(RankingPeriod.WEEKLY)
    }

    fun updatePeriod(period: RankingPeriod) {
        if (_uiState.value.selectedPeriod == period) return
        loadPosts(period)
    }

    private fun loadPosts(period: RankingPeriod) {
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedPeriod = period) }

            postUseCases.getMostBookmarkedPosts(period = period, limit = 50)
                .collect { result ->
                    if (result is DataResourceResult.Success) {
                        val uiModels = result.data.map { it.toUiModel() }
                        _uiState.update {
                            it.copy(isLoading = false, posts = uiModels)
                        }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "데이터를 불러오지 못했습니다.")
                        }
                    }
                }
        }
    }

    fun toggleBookmark(post: CommunityPostUiModel) {
        val postId = post.postId
        val currentBookmarked = post.isBookmarked
        val newBookmarked = !currentBookmarked

        updatePostState(postId, newBookmarked)

        viewModelScope.launch {
            val result = postUseCases.toggleBookmark(postId)
            if (result is DataResourceResult.Failure) {
                // 실패 시 롤백
                updatePostState(postId, currentBookmarked)
                _uiState.update { it.copy(errorMessage = "북마크 변경 실패") }
            }
        }
    }


    private fun updatePostState(postId: String, isBookmarked: Boolean) {
        _uiState.update { state ->
            val updatedPosts = state.posts.map { item ->
                if (item.postId == postId) {
                    val currentCount = item.bookmarkCount.toIntOrNull() ?: 0
                    val newCount = (if (isBookmarked) currentCount + 1 else maxOf(0, currentCount - 1)).toString()
                    item.updateBookmarkState(isBookmarked, newCount)
                } else {
                    item
                }
            }
            state.copy(posts = updatedPosts)
        }
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