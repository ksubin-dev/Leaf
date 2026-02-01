package com.leafy.features.mypage.presentation.tea.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.R
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.model.TeaItem
import com.subin.leafy.domain.usecase.TeaUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MyTeaListSideEffect {
    data class ShowToast(val message: UiText) : MyTeaListSideEffect
}

data class MyTeaListUiState(
    val isLoading: Boolean = false,
    val teaList: List<TeaItem> = emptyList()
)

@HiltViewModel
class MyTeaListViewModel @Inject constructor(
    private val teaUseCases: TeaUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyTeaListUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<MyTeaListSideEffect>()
    val sideEffect: Flow<MyTeaListSideEffect> = _sideEffect.receiveAsFlow()

    init {
        loadTeas()
    }

    private fun loadTeas() {
        _uiState.update { it.copy(isLoading = true) }

        teaUseCases.getTeas()
            .catch { e ->
                _uiState.update { it.copy(isLoading = false) }
                val msg = e.message
                val uiText = if (msg != null) UiText.DynamicString(msg)
                else UiText.StringResource(R.string.msg_error_occurred)
                sendEffect(MyTeaListSideEffect.ShowToast(uiText))
            }
            .onEach { teas ->
                _uiState.update {
                    it.copy(isLoading = false, teaList = teas)
                }
            }
            .launchIn(viewModelScope)
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

    private fun sendEffect(effect: MyTeaListSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }
}