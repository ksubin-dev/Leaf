package com.leafy.features.community.presentation.screen.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.ui.mapper.toUiModel
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.PostUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PopularPostListViewModel(
    private val postUseCases: PostUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(PopularPostListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            postUseCases.getPopularPosts(limit = 50).collect { result ->
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
}
