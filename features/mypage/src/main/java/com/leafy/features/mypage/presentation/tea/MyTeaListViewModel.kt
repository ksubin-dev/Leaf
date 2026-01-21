package com.leafy.features.mypage.presentation.tea

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.usecase.TeaUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MyTeaListUiState(
    val isLoading: Boolean = false,
    val teaList: List<TeaItem> = emptyList(),
    val errorMessage: String? = null
)

class MyTeaListViewModel(
    private val teaUseCases: TeaUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyTeaListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTeas()
    }

    private fun loadTeas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            teaUseCases.getTeas()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
                .collectLatest { teas ->
                    _uiState.update {
                        it.copy(isLoading = false, teaList = teas)
                    }
                }
        }
    }

    fun toggleFavorite(tea: TeaItem) {
        viewModelScope.launch {
            teaUseCases.toggleFavorite(tea.id)
        }
    }

    fun deleteTea(tea: TeaItem) {
        viewModelScope.launch {
            teaUseCases.deleteTea(tea.id)
        }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}