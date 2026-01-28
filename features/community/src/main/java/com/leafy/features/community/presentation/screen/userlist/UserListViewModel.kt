package com.leafy.features.community.presentation.screen.userlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.leafy.shared.navigation.MainNavigationRoute
import com.leafy.shared.navigation.UserListType
import com.leafy.shared.ui.mapper.toUiModel
import com.leafy.shared.ui.model.UserUiModel
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.UserUseCases
import com.subin.leafy.domain.usecase.user.FollowType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface UserListSideEffect {
    data class ShowSnackbar(val message: UiText) : UserListSideEffect
}

data class UserListUiState(
    val isLoading: Boolean = true,
    val users: List<UserUiModel> = emptyList(),
    val currentUserId: String? = null,
    val listType: UserListType = UserListType.FOLLOWER
)

@HiltViewModel
class UserListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val route = savedStateHandle.toRoute<MainNavigationRoute.UserList>()
    private val targetUserId = route.userId
    private val listType = route.type

    private val _uiState = MutableStateFlow(UserListUiState(listType = listType))
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<UserListSideEffect>()
    val sideEffect: Flow<UserListSideEffect> = _sideEffect.receiveAsFlow()

    init {
        loadCurrentUserId()
        loadUserList()
    }

    private fun loadCurrentUserId() {
        viewModelScope.launch {
            val result = userUseCases.getCurrentUserId()
            if (result is DataResourceResult.Success) {
                _uiState.update { it.copy(currentUserId = result.data) }
            }
        }
    }

    private fun loadUserList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val domainType = when (listType) {
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
                    _uiState.update { it.copy(isLoading = false) }
                    sendEffect(UserListSideEffect.ShowSnackbar(UiText.DynamicString("리스트를 불러오지 못했습니다.")))
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
                sendEffect(UserListSideEffect.ShowSnackbar(UiText.DynamicString("팔로우 변경 실패")))
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

    private fun sendEffect(effect: UserListSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }
}