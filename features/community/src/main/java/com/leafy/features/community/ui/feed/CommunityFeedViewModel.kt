package com.leafy.features.community.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.features.community.ui.mapper.toUiModel
import com.leafy.features.community.ui.model.CommentUiModel
import com.leafy.features.community.ui.model.CommunityPostUiModel
import com.leafy.features.community.ui.model.UserUiModel
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.Comment
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface CommunityFeedSideEffect {
    object HideKeyboard : CommunityFeedSideEffect
    //object ScrollToBottom : CommunityFeedSideEffect 같은 것도
}

@OptIn(ExperimentalCoroutinesApi::class)
class CommunityFeedViewModel(
    private val postUseCases: PostUseCases,
    private val userUseCases: UserUseCases
) : ViewModel() {
    private val _sideEffects = Channel<CommunityFeedSideEffect>()
    val sideEffects = _sideEffects.receiveAsFlow()

    private val _selectedTab = MutableStateFlow(CommunityTab.TRENDING)

    private val _uiControlState = MutableStateFlow(UiControlState())

    private data class UiControlState(
        val showCommentSheet: Boolean = false,
        val selectedPostIdForComments: String? = null,
        val comments: List<CommentUiModel> = emptyList(),
        val commentInput: String = "",
        val isCommentLoading: Boolean = false,
        val errorMessage: String? = null
    )

    private val followingFeedFlow: Flow<DataResourceResult<List<CommunityPostUiModel>>> = run {
        val idResult = userUseCases.getCurrentUserId()
        val myId = if (idResult is DataResourceResult.Success) idResult.data else ""

        if (myId.isEmpty()) {
            flowOf(DataResourceResult.Success(emptyList()))
        } else {
            userUseCases.getFollowingIdsFlow(myId)
                .map { result ->
                    if (result is DataResourceResult.Success) result.data else emptyList()
                }
                .distinctUntilChanged()
                .flatMapLatest { ids ->
                    postUseCases.getFollowingFeed(ids)
                }
                .map { result ->
                    when (result) {
                        is DataResourceResult.Success -> {
                            DataResourceResult.Success(result.data.map { it.toUiModel() })
                        }
                        is DataResourceResult.Failure -> {
                            DataResourceResult.Failure(result.exception)
                        }
                        is DataResourceResult.Loading -> {
                            DataResourceResult.Loading
                        }
                    }
                }
        }
    }

    private var cachedPopularPosts: List<CommunityPostUiModel> = emptyList()
    private var cachedMostBookmarked: List<CommunityPostUiModel> = emptyList()
    private var cachedTeaMasters: List<UserUiModel> = emptyList()
    private var cachedFollowingPosts: List<CommunityPostUiModel> = emptyList()

    val uiState: StateFlow<CommunityUiState> = combine(
        _selectedTab,
        _uiControlState,
        postUseCases.getPopularPosts(),
        postUseCases.getMostBookmarkedPosts(),
        postUseCases.getRecommendedMasters(),
        followingFeedFlow
    ) { tab, uiControl, popularResult, bookmarkResult, masterResult, followingResult ->

        if (popularResult is DataResourceResult.Success) {
            cachedPopularPosts = popularResult.data.map { it.toUiModel() }
        }
        if (bookmarkResult is DataResourceResult.Success) {
            cachedMostBookmarked = bookmarkResult.data.map { it.toUiModel() }
        }
        if (masterResult is DataResourceResult.Success) {
            cachedTeaMasters = masterResult.data.map { it.toUiModel() }
        }

        if (followingResult is DataResourceResult.Success) {
            cachedFollowingPosts = followingResult.data
        }

        val hasTrendingData = cachedPopularPosts.isNotEmpty() || cachedMostBookmarked.isNotEmpty()
        val hasFollowingData = cachedFollowingPosts.isNotEmpty()

        val hasDataToShow = when(tab) {
            CommunityTab.TRENDING -> hasTrendingData
            CommunityTab.FOLLOWING -> hasFollowingData
        }

        val isLoading = when (tab) {
            CommunityTab.TRENDING -> popularResult is DataResourceResult.Loading
                    || bookmarkResult is DataResourceResult.Loading
                    || masterResult is DataResourceResult.Loading
            CommunityTab.FOLLOWING -> followingResult is DataResourceResult.Loading
        }

        var currentError = uiControl.errorMessage

        if (currentError == null && !isLoading) {
            val isFailure = when (tab) {
                CommunityTab.TRENDING -> popularResult is DataResourceResult.Failure
                        || bookmarkResult is DataResourceResult.Failure
                        || masterResult is DataResourceResult.Failure
                CommunityTab.FOLLOWING -> followingResult is DataResourceResult.Failure
            }

            if (isFailure) {
                if (hasDataToShow) {
                    currentError = null
                } else {
                    currentError = "데이터를 불러오지 못했습니다."
                }
            }
        }

        val isFollowingEmpty = followingResult is DataResourceResult.Success && cachedFollowingPosts.isEmpty()

        CommunityUiState(
            selectedTab = tab,
            isLoading = isLoading,
            errorMessage = currentError,

            popularPosts = cachedPopularPosts,
            mostBookmarkedPosts = cachedMostBookmarked,
            teaMasters = cachedTeaMasters,

            followingPosts = cachedFollowingPosts,
            isFollowingEmpty = isFollowingEmpty,

            commentInput = uiControl.commentInput,
            showCommentSheet = uiControl.showCommentSheet,
            selectedPostId = uiControl.selectedPostIdForComments,
            comments = uiControl.comments,
            isCommentLoading = uiControl.isCommentLoading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CommunityUiState(isLoading = true)
    )
    private var commentJob: Job? = null

    fun onTabSelected(tab: CommunityTab) {
        _selectedTab.value = tab
    }

    fun refresh() {
        val currentTab = _selectedTab.value
        _selectedTab.value = if (currentTab == CommunityTab.TRENDING) CommunityTab.FOLLOWING else CommunityTab.TRENDING
        _selectedTab.value = currentTab
        _uiControlState.update { it.copy(errorMessage = null) }
    }

    fun updateCommentInput(input: String) {
        _uiControlState.update { it.copy(commentInput = input) }
    }

    fun toggleFollow(targetUserId: String) {
        // 1. 현재 리스트에서 해당 유저를 찾아서 팔로우 상태 확인
        val currentMaster = cachedTeaMasters.find { it.userId == targetUserId } ?: return
        val nextState = !currentMaster.isFollowing // 현재 상태의 반대 (Toggle)

        viewModelScope.launch {
            // 2. UseCase 호출 (아이디, 바꿀 상태)
            val result = userUseCases.followUser(targetUserId, nextState)

            if (result is DataResourceResult.Failure) {
                _uiControlState.update {
                    it.copy(errorMessage = result.exception.message ?: "팔로우 요청 실패")
                }
                // 실패했다면 여기서 다시 UI를 원복하는 로직이 필요할 수도 있음 (낙관적 업데이트 시)
            }

            // 성공 시:
            // 아래 Repository 로직이 잘 되어 있다면 자동으로 Flow가 갱신됩니다.
        }
    }

    fun toggleLike(postId: String) {
        viewModelScope.launch {
            val result = postUseCases.toggleLike(postId)
            if (result is DataResourceResult.Failure) {
                _uiControlState.update {
                    it.copy(errorMessage = result.exception.message ?: "좋아요 처리에 실패했습니다.")
                }
            }
        }
    }

    fun toggleBookmark(postId: String) {
        viewModelScope.launch {
            postUseCases.toggleBookmark(postId)
        }
    }

    fun showComments(postId: String) {
        _uiControlState.update { it.copy(showCommentSheet = true, selectedPostIdForComments = postId) }
        loadComments(postId)
    }

    fun hideComments() {
        commentJob?.cancel()
        _uiControlState.update { it.copy(showCommentSheet = false, selectedPostIdForComments = null, comments = emptyList()) }
    }

    private fun loadComments(postId: String) {
        _uiControlState.update { it.copy(isCommentLoading = true) }

        commentJob?.cancel()
        commentJob = postUseCases.getComments(postId).onEach { result ->
            when (result) {
                is DataResourceResult.Success -> {
                    val uiModels = result.data.map { it.toUiModel() }

                    _uiControlState.update {
                        it.copy(comments = uiModels, isCommentLoading = false)
                    }
                }
                is DataResourceResult.Failure -> {
                    _uiControlState.update {
                        it.copy(isCommentLoading = false, errorMessage = "댓글을 불러오지 못했습니다.")
                    }
                }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun addComment() {
        val postId = _uiControlState.value.selectedPostIdForComments ?: return
        val content = _uiControlState.value.commentInput

        if (content.isBlank()) return

        viewModelScope.launch {
            _uiControlState.update { it.copy(isCommentLoading = true) }

            when (val result = postUseCases.addComment(postId, content)) {
                is DataResourceResult.Success -> {
                    _uiControlState.update {
                        it.copy(
                            commentInput = "",
                            isCommentLoading = false
                        )
                    }
                    _sideEffects.send(CommunityFeedSideEffect.HideKeyboard)
                }
                is DataResourceResult.Failure -> {
                    _uiControlState.update {
                        it.copy(
                            errorMessage = result.exception.message ?: "댓글 등록 실패",
                            isCommentLoading = false
                        )
                    }
                }
                else -> {}
            }
        }
    }

    fun deleteComment(commentId: String) {
        val postId = _uiControlState.value.selectedPostIdForComments ?: return
        viewModelScope.launch {
            postUseCases.deleteComment(postId, commentId)
        }
    }

    fun onMessageShown() {
        _uiControlState.update { it.copy(errorMessage = null) }
    }
}

@Suppress("UNCHECKED_CAST")
fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R
): Flow<R> = combine(flow1, flow2, flow3, flow4, flow5, flow6) { args ->
    transform(
        args[0] as T1,
        args[1] as T2,
        args[2] as T3,
        args[3] as T4,
        args[4] as T5,
        args[5] as T6
    )
}