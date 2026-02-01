package com.leafy.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.R
import com.leafy.shared.ui.mapper.toUiModel
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SearchSideEffect {
    data class ShowToast(val message: UiText) : SearchSideEffect
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val postUseCases: PostUseCases,
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<SearchSideEffect>()
    val sideEffect: Flow<SearchSideEffect> = _sideEffect.receiveAsFlow()

    private val _queryFlow = MutableStateFlow("")

    init {
        observeQueryChanges()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeQueryChanges() {
        _queryFlow
            .debounce(5000L)
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .onEach { query ->
                performSearch(query)
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }

        _queryFlow.value = newQuery

        if (newQuery.isBlank()) {
            _uiState.update {
                it.copy(
                    postResults = emptyList(),
                    userResults = emptyList(),
                    isLoading = false
                )
            }
        }
    }

    fun onTabSelected(tab: SearchTab) {
        if (_uiState.value.selectedTab == tab) return
        _uiState.update { it.copy(selectedTab = tab) }

        val currentQuery = _uiState.value.query
        if (currentQuery.isNotBlank()) {
            performSearch(currentQuery)
        }
    }

    fun onSearchAction() {
        val query = _uiState.value.query
        if (query.isNotBlank()) {
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    postResults = if (it.selectedTab == SearchTab.POSTS) emptyList() else it.postResults,
                    userResults = if (it.selectedTab == SearchTab.USERS) emptyList() else it.userResults
                )
            }

            if (_uiState.value.selectedTab == SearchTab.POSTS) {
                searchPosts(query)
            } else {
                searchUsers(query)
            }
        }
    }

    private suspend fun searchPosts(query: String) {
        when (val result = postUseCases.searchPosts(query)) {
            is DataResourceResult.Success -> {
                val uiModels = result.data.map { it.toUiModel() }
                _uiState.update { it.copy(postResults = uiModels, isLoading = false) }
            }
            is DataResourceResult.Failure -> {
                _uiState.update { it.copy(isLoading = false) }
                sendEffect(SearchSideEffect.ShowToast(UiText.StringResource(R.string.msg_search_failed)))
            }
            else -> { _uiState.update { it.copy(isLoading = false) } }
        }
    }

    private suspend fun searchUsers(query: String) {
        when (val result = userUseCases.searchUsers(query)) {
            is DataResourceResult.Success -> {
                val uiModels = result.data.map { it.toUiModel() }
                _uiState.update { it.copy(userResults = uiModels, isLoading = false) }
            }
            is DataResourceResult.Failure -> {
                _uiState.update { it.copy(isLoading = false) }
                sendEffect(SearchSideEffect.ShowToast(UiText.StringResource(R.string.msg_search_failed)))
            }
            else -> { _uiState.update { it.copy(isLoading = false) } }
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
                val lastId = state.postResults.lastOrNull()?.postId ?: return@launch
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
                val lastId = state.userResults.lastOrNull()?.userId ?: return@launch
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

    private fun sendEffect(effect: SearchSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }
}