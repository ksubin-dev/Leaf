package com.leafy.features.community.presentation.screen.teamaster

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.ui.mapper.toUiModel
import com.leafy.shared.ui.model.UserUiModel
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TeaMasterListViewModel(
    private val postUseCases: PostUseCases,
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeaMasterListUiState())
    val uiState = _uiState.asStateFlow()

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
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "데이터를 불러오지 못했습니다.")
                    }
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
                _uiState.update { state ->
                    state.copy(masters = currentList)
                }
            }
        }
    }
}