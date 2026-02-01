package com.leafy.features.mypage.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.usecase.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class MyRecordSearchUiState(
    val searchQuery: String = "",
    val searchResultNotes: List<BrewingNote> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class MyRecordSearchViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyRecordSearchUiState())
    val uiState = _uiState.asStateFlow()

    private val _searchQueryFlow = MutableStateFlow("")

    init {
        observeSearchQuery()
        onSearchQueryChange("")
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun observeSearchQuery() {
        _searchQueryFlow
            .debounce(300L)
            .flatMapLatest { query ->
                if (query.isBlank()) {
                    noteUseCases.getMyNotes()
                } else {
                    noteUseCases.searchMyNotes(query)
                }
            }
            .onEach { notes ->
                _uiState.update { it.copy(searchResultNotes = notes) }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchQueryFlow.value = query
    }
}