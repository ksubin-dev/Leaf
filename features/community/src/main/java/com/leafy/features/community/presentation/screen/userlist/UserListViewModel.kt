package com.leafy.features.community.presentation.screen.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.features.community.presentation.common.mapper.toUiModel
import com.leafy.features.community.presentation.common.model.UserUiModel
import com.leafy.shared.navigation.UserListType
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.UserUseCases
import com.subin.leafy.domain.usecase.user.FollowType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserListUiState(
    val isLoading: Boolean = true,
    val users: List<UserUiModel> = emptyList(),
    val errorMessage: String? = null,
    val currentUserId: String? = null
)

class UserListViewModel(
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserListUiState())
    val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

    init {
        loadCurrentUserId()
    }

    private fun loadCurrentUserId() {
        viewModelScope.launch {
            val result = userUseCases.getCurrentUserId()
            if (result is DataResourceResult.Success) {
                _uiState.update { it.copy(currentUserId = result.data) }
            }
        }
    }

    fun loadUserList(targetUserId: String, type: UserListType) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val domainType = when (type) {
                UserListType.FOLLOWER -> FollowType.FOLLOWER
                UserListType.FOLLOWING -> FollowType.FOLLOWING
            }

            val result = userUseCases.getFollowList(targetUserId, domainType)

            when (result) {
                is DataResourceResult.Success -> {
                    val userUiModels = result.data.map { it.toUiModel() }
                    _uiState.update {
                        it.copy(isLoading = false, users = userUiModels)
                    }
                }
                is DataResourceResult.Failure -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "리스트를 불러오지 못했습니다.")
                    }
                }
                else -> {}
            }
        }
    }

    fun toggleFollow(targetUser: UserUiModel) {
        val nextState = !targetUser.isFollowing
        updateFollowStateLocal(targetUser.userId, nextState)

        viewModelScope.launch {
            val result = userUseCases.followUser(targetUser.userId, nextState)

            if (result is DataResourceResult.Failure) {
                updateFollowStateLocal(targetUser.userId, !nextState)
            }
        }
    }

    private fun updateFollowStateLocal(userId: String, isFollowing: Boolean) {
        _uiState.update { state ->
            val updatedList = state.users.map { user ->
                if (user.userId == userId) user.copy(isFollowing = isFollowing) else user
            }
            state.copy(users = updatedList)
        }
    }
}