package com.leafy.features.community.presentation.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.features.community.presentation.common.model.CommunityPostUiModel
import com.leafy.features.community.presentation.common.model.UserUiModel
import com.leafy.features.community.presentation.common.mapper.toUiModel
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UserProfileUiState(
    val isLoading: Boolean = true,
    val userProfile: UserUiModel? = null,
    val userPosts: List<CommunityPostUiModel> = emptyList(),
    val isFollowing: Boolean = false,
    val isMe: Boolean = false,
    val errorMessage: String? = null
)

class UserProfileViewModel(
    private val targetUserId: String,
    private val userUseCases: UserUseCases,
    private val postUseCases: PostUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileUiState())
    val uiState = _uiState.asStateFlow()

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
                        isFollowing = isFollowingInitial
                    )
                }

                postUseCases.getUserPosts(targetUserId)
                    .onEach { result ->
                        if (result is DataResourceResult.Success) {
                            val uiPosts = result.data.map { it.toUiModel() }
                            _uiState.update { it.copy(userPosts = uiPosts) }
                        }
                    }
                    .launchIn(viewModelScope)

                if (!isMe && myId.isNotEmpty()) {
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
                _uiState.update { it.copy(isLoading = false) }

            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "유저를 찾을 수 없습니다.") }
            }
        }
    }

    fun toggleFollow() {
        if (_uiState.value.isMe) return

        val currentFollow = _uiState.value.isFollowing
        val newFollowState = !currentFollow

        _uiState.update { it.copy(isFollowing = newFollowState) }

        viewModelScope.launch {
            val result = userUseCases.followUser(targetUserId, newFollowState)

            if (result is DataResourceResult.Failure) {
                _uiState.update {
                    it.copy(
                        isFollowing = currentFollow,
                        errorMessage = "작업을 완료하지 못했습니다."
                    )
                }
            }
        }
    }
}