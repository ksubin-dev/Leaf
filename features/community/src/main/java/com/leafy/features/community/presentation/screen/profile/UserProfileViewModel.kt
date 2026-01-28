package com.leafy.features.community.presentation.screen.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.leafy.shared.navigation.MainNavigationRoute
import com.leafy.shared.ui.mapper.toUiModel
import com.leafy.shared.ui.model.CommunityPostUiModel
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

sealed interface UserProfileSideEffect {
    data class ShowSnackbar(val message: UiText) : UserProfileSideEffect
}

data class UserProfileUiState(
    val isLoading: Boolean = true,
    val userProfile: UserUiModel? = null,
    val userPosts: List<CommunityPostUiModel> = emptyList(),
    val isFollowing: Boolean = false,
    val isMe: Boolean = false,
    val errorMessage: UiText? = null
)

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userUseCases: UserUseCases,
    private val postUseCases: PostUseCases
) : ViewModel() {

    private val targetUserId: String = savedStateHandle.toRoute<MainNavigationRoute.UserProfile>().userId

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<UserProfileSideEffect>()
    val sideEffect: Flow<UserProfileSideEffect> = _sideEffect.receiveAsFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val myIdResult = userUseCases.getCurrentUserId()
            val myId = if (myIdResult is DataResourceResult.Success) myIdResult.data else ""
            val isMe = (myId == targetUserId)

            val userResult = userUseCases.getUserProfile(targetUserId)

            if (userResult is DataResourceResult.Success) {
                val userDomain = userResult.data
                val isFollowingInitial = userDomain.relationState.isFollowing

                _uiState.update {
                    it.copy(
                        userProfile = userDomain.toUiModel(),
                        isMe = isMe,
                        isFollowing = isFollowingInitial,
                        isLoading = false
                    )
                }

                loadUserPosts()

                if (!isMe && myId.isNotEmpty()) {
                    observeFollowState(myId)
                }

            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = UiText.DynamicString("유저를 찾을 수 없습니다.")
                    )
                }
            }
        }
    }

    private fun loadUserPosts() {
        postUseCases.getUserPosts(targetUserId)
            .onEach { result ->
                if (result is DataResourceResult.Success) {
                    val uiPosts = result.data.map { it.toUiModel() }
                    _uiState.update { it.copy(userPosts = uiPosts) }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeFollowState(myId: String) {
        userUseCases.getFollowingIdsFlow(myId)
            .onEach { result ->
                if (result is DataResourceResult.Success) {
                    val followingIds = result.data
                    val isFollowing = followingIds.contains(targetUserId)
                    _uiState.update { it.copy(isFollowing = isFollowing) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun toggleFollow() {
        if (_uiState.value.isMe) return

        val currentFollow = _uiState.value.isFollowing
        val newFollowState = !currentFollow

        _uiState.update { it.copy(isFollowing = newFollowState) }

        viewModelScope.launch {
            val result = userUseCases.followUser(targetUserId, newFollowState)

            if (result is DataResourceResult.Failure) {
                _uiState.update { it.copy(isFollowing = currentFollow) }
                sendEffect(UserProfileSideEffect.ShowSnackbar(UiText.DynamicString("작업을 완료하지 못했습니다.")))
            }
        }
    }

    private fun sendEffect(effect: UserProfileSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }
}