package com.leafy.features.community.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.*
import com.subin.leafy.domain.usecase.CommunityUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface CommunityUiEffect {
    data class ShowSnackbar(val message: String) : CommunityUiEffect
    data class NavigateToComments(val postId: String) : CommunityUiEffect
}

class CommunityViewModel(
    private val communityUseCases: CommunityUseCases
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(ExploreTab.TRENDING)
    private val _comments = MutableStateFlow<List<CommunityCommentUi>>(emptyList())
    private val _isCommentsLoading = MutableStateFlow(false)

    private val _effect = MutableSharedFlow<CommunityUiEffect>()
    val effect = _effect.asSharedFlow()

    val uiState: StateFlow<CommunityUiState> = combine(
        communityUseCases.getPopularNotes(),        // [0]
        communityUseCases.getMostSavedNotes(),      // [1]
        communityUseCases.getRecommendedMasters(),  // [2]
        communityUseCases.getFollowingFeed(),       // [3]
        _selectedTab,                               // [4]
        _comments,                                  // [5]
        _isCommentsLoading                          // [6]
    ) { array ->
        val popular = array[0] as DataResourceResult<List<CommunityPost>>
        val saved = array[1] as DataResourceResult<List<CommunityPost>>
        val masters = array[2] as DataResourceResult<List<TeaMaster>>
        val following = array[3] as DataResourceResult<List<CommunityPost>>
        val tab = array[4] as ExploreTab
        val comments = array[5] as List<CommunityCommentUi>
        val isCommentsLoading = array[6] as Boolean

        val allResults = listOf(popular, saved, masters, following)

        CommunityUiState(
            isLoading = allResults.any { it is DataResourceResult.Loading },
            selectedTab = tab,
            popularNotes = (popular as? DataResourceResult.Success)?.data?.toNoteUi() ?: emptyList(),
            mostSavedNotes = (saved as? DataResourceResult.Success)?.data?.toNoteUi() ?: emptyList(),
            teaMasters = (masters as? DataResourceResult.Success)?.data?.toMasterUi() ?: emptyList(),
            followingFeed = (following as? DataResourceResult.Success)?.data?.toNoteUi() ?: emptyList(),
            comments = comments,
            isCommentsLoading = isCommentsLoading,
            errorMessage = allResults
                .filterIsInstance<DataResourceResult.Failure>()
                .firstOrNull()?.exception?.message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CommunityUiState(isLoading = true)
    )

    // --- [인터랙션 함수] ---

    fun onTabSelected(tab: ExploreTab) {
        _selectedTab.value = tab
    }

    fun toggleLike(postId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            val result = communityUseCases.toggleLike(postId, currentStatus)
            handleResult(result, "좋아요 처리 실패")
        }
    }

    fun toggleSave(postId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            val result = communityUseCases.toggleSave(postId, currentStatus)
            handleResult(result, "저장 실패")
        }
    }

    fun toggleFollow(masterId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            val result = communityUseCases.toggleFollow(masterId, currentStatus)
            handleResult(result, "팔로우 처리 실패")
        }
    }

    // --- [댓글 관련] ---

    fun loadComments(postId: String) {
        viewModelScope.launch {
            _isCommentsLoading.value = true
            communityUseCases.getComments(postId).collect { result ->
                if (result is DataResourceResult.Success) {
                    _comments.value = result.data.toCommentUi()
                }
                _isCommentsLoading.value = false
            }
        }
    }

    fun sendComment(postId: String, content: String) {
        viewModelScope.launch {
            val result = communityUseCases.addComment(postId, content)
            if (result is DataResourceResult.Success) {
                loadComments(postId)
            } else if (result is DataResourceResult.Failure) {
                handleResult(result, "댓글 작성 실패")
            }
        }
    }

    fun onCommentClick(postId: String) {
        viewModelScope.launch {
            _effect.emit(CommunityUiEffect.NavigateToComments(postId))
        }
    }

    private suspend fun handleResult(result: DataResourceResult<Unit>, errorPrefix: String) {
        if (result is DataResourceResult.Failure) {
            _effect.emit(CommunityUiEffect.ShowSnackbar("$errorPrefix: ${result.exception.message}"))
        }
    }
}