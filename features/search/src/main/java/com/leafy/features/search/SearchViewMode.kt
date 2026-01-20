package com.leafy.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.leafy.shared.ui.mapper.toUiModel

class SearchViewModel(
    private val postUseCases: PostUseCases,
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }

        searchJob?.cancel()

        if (newQuery.isBlank()) {
            _uiState.update {
                it.copy(
                    postResults = emptyList(),
                    userResults = emptyList(),
                    isLoading = false,
                    errorMessage = null
                )
            }
            return
        }

        searchJob = viewModelScope.launch {
            delay(500L)
            performSearch()
        }
    }

    fun onTabSelected(tab: SearchTab) {
        if (_uiState.value.selectedTab == tab) return
        _uiState.update { it.copy(selectedTab = tab) }

        // 탭 변경 시 검색어가 있다면 바로 검색 (기다릴 필요 없음)
        if (_uiState.value.query.isNotBlank()) {
            performSearch()
        }
    }

    fun performSearch() {
        val query = _uiState.value.query
        if (query.isBlank()) return

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    postResults = if (it.selectedTab == SearchTab.POSTS) emptyList() else it.postResults,
                    userResults = if (it.selectedTab == SearchTab.USERS) emptyList() else it.userResults
                )
            }

            if (_uiState.value.selectedTab == SearchTab.POSTS) {
                when (val result = postUseCases.searchPosts(query)) {
                    is DataResourceResult.Success -> {
                        val uiModels = result.data.map { it.toUiModel() }
                        _uiState.update {
                            it.copy(postResults = uiModels, isLoading = false)
                        }
                    }
                    is DataResourceResult.Failure -> {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "검색에 실패했습니다.")
                        }
                    }
                    else -> {}
                }
            } else {
                when (val result = userUseCases.searchUsers(query)) {
                    is DataResourceResult.Success -> {
                        val uiModels = result.data.map { it.toUiModel() }
                        _uiState.update {
                            it.copy(userResults = uiModels, isLoading = false)
                        }
                    }
                    is DataResourceResult.Failure -> {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "검색에 실패했습니다.")
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || state.isLastPage) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }

            val query = state.query
            val limit = 20

            if (state.selectedTab == SearchTab.POSTS) {
                val lastId = state.postResults.lastOrNull()?.postId

                if (lastId == null) {
                    _uiState.update { it.copy(isLoadingMore = false) }
                    return@launch
                }

                val result = postUseCases.searchPosts(query, lastId, limit)
                if (result is DataResourceResult.Success) {
                    val newItems = result.data.map { it.toUiModel() }
                    _uiState.update { prev ->
                        prev.copy(
                            postResults = prev.postResults + newItems,
                            isLoadingMore = false,
                            isLastPage = newItems.size < limit
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoadingMore = false) }
                }
            } else {
                val lastId = state.userResults.lastOrNull()?.userId
                if (lastId == null) {
                    _uiState.update { it.copy(isLoadingMore = false) }
                    return@launch
                }

                val result = userUseCases.searchUsers(query, lastId, limit)
                if (result is DataResourceResult.Success) {
                    val newItems = result.data.map { it.toUiModel() }
                    _uiState.update { prev ->
                        prev.copy(
                            userResults = prev.userResults + newItems,
                            isLoadingMore = false,
                            isLastPage = newItems.size < limit
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoadingMore = false) }
                }
            }
        }
    }
}