package com.leafy.features.community.presentation.screen.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.ui.mapper.toUiModel
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.PostUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PopularPostListSideEffect {
    data class ShowSnackbar(val message: UiText) : PopularPostListSideEffect
}

@HiltViewModel
class PopularPostListViewModel @Inject constructor(
    private val postUseCases: PostUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(PopularPostListUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<PopularPostListSideEffect>()
    val sideEffect: Flow<PopularPostListSideEffect> = _sideEffect.receiveAsFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            postUseCases.getPopularPosts(limit = 50).collect { result ->
                when (result) {
                    is DataResourceResult.Success -> {
                        val uiModels = result.data.map { it.toUiModel() }
                        _uiState.update {
                            it.copy(isLoading = false, posts = uiModels)
                        }
                    }
                    is DataResourceResult.Failure -> {
                        _uiState.update { it.copy(isLoading = false) }
                        sendEffect(PopularPostListSideEffect.ShowSnackbar(
                            UiText.DynamicString("데이터를 불러오지 못했습니다.")
                        ))
                    }
                    else -> {}
                }
            }
        }
    }

    private fun sendEffect(effect: PopularPostListSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }
}
