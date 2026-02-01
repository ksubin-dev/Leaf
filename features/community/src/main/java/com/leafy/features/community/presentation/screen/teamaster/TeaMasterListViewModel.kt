package com.leafy.features.community.presentation.screen.teamaster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.R
import com.leafy.shared.ui.mapper.toUiModel
import com.leafy.shared.ui.model.UserUiModel
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TeaMasterListSideEffect {
    data class ShowToast(val message: UiText) : TeaMasterListSideEffect
}

data class TeaMasterListUiState(
    val isLoading: Boolean = true,
    val masters: List<UserUiModel> = emptyList(),
    val currentUserId: String? = null
)

@HiltViewModel
class TeaMasterListViewModel @Inject constructor(
    private val postUseCases: PostUseCases,
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeaMasterListUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<TeaMasterListSideEffect>()
    val sideEffect: Flow<TeaMasterListSideEffect> = _sideEffect.receiveAsFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val myIdResult = userUseCases.getCurrentUserId()
            val myId = if (myIdResult is DataResourceResult.Success) myIdResult.data else null

            postUseCases.getRecommendedMasters(limit = 50).collect { result ->
                if (result is DataResourceResult.Success) {
                    val uiModels = result.data.map { it.toUiModel() }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            masters = uiModels,
                            currentUserId = myId
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                    sendEffect(TeaMasterListSideEffect.ShowToast(
                        UiText.StringResource(R.string.msg_data_load_error)
                    ))
                }
            }
        }
    }

    fun toggleFollow(targetUser: UserUiModel) {
        val currentList = _uiState.value.masters
        val nextState = !targetUser.isFollowing

        _uiState.update { state ->
            state.copy(
                masters = state.masters.map {
                    if (it.userId == targetUser.userId) it.copy(isFollowing = nextState)
                    else it
                }
            )
        }

        viewModelScope.launch {
            val result = userUseCases.followUser(targetUser.userId, nextState)
            if (result is DataResourceResult.Failure) {
                _uiState.update { state -> state.copy(masters = currentList) }
                sendEffect(TeaMasterListSideEffect.ShowToast(
                    UiText.StringResource(R.string.msg_follow_failed)
                ))
            } else {
                val msgResId = if (nextState) R.string.msg_follow_success else R.string.msg_unfollow_success
                sendEffect(TeaMasterListSideEffect.ShowToast(
                    UiText.StringResource(msgResId)
                ))
            }
        }
    }

    private fun sendEffect(effect: TeaMasterListSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }
}