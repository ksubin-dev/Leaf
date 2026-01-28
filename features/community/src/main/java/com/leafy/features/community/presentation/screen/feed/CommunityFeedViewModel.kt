package com.leafy.features.community.presentation.screen.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.features.community.presentation.components.bar.CommunityTab
import com.leafy.shared.ui.mapper.toUiModel
import com.leafy.shared.ui.model.CommentUiModel
import com.leafy.shared.ui.model.CommunityPostUiModel
import com.leafy.shared.ui.model.UserUiModel
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.PostChangeEvent
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CommunityFeedSideEffect {
    data object HideKeyboard : CommunityFeedSideEffect
    data class ShowSnackbar(val message: UiText) : CommunityFeedSideEffect
}

@HiltViewModel
class CommunityFeedViewModel @Inject constructor(
    private val postUseCases: PostUseCases,
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _sideEffect = Channel<CommunityFeedSideEffect>()
    val sideEffect: Flow<CommunityFeedSideEffect> = _sideEffect.receiveAsFlow()

    private val _selectedTab = MutableStateFlow(CommunityTab.TRENDING)
    private val _trendingPosts = MutableStateFlow<List<CommunityPostUiModel>>(emptyList())
    private val _bookmarkedPosts = MutableStateFlow<List<CommunityPostUiModel>>(emptyList())
    private val _followingPosts = MutableStateFlow<List<CommunityPostUiModel>>(emptyList())
    private val _teaMasters = MutableStateFlow<List<UserUiModel>>(emptyList())

    // User Data (Internal)
    private val _myLikedIds = MutableStateFlow<Set<String>>(emptySet())
    private val _myBookmarkedIds = MutableStateFlow<Set<String>>(emptySet())
    private val _currentUserId = MutableStateFlow<String?>(null)
    private val _currentUserProfileUrl = MutableStateFlow<String?>(null)

    // UI Control State
    private data class UiControlState(
        val isInitialLoading: Boolean = true,
        val errorMessage: String? = null,
        val showCommentSheet: Boolean = false,
        val selectedPostIdForComments: String? = null,
        val comments: List<CommentUiModel> = emptyList(),
        val commentInput: String = "",
        val isCommentLoading: Boolean = false
    )
    private val _uiControlState = MutableStateFlow(UiControlState())

    init {
        loadMyUserData()
        loadInitialData()
        observePostChanges()
    }

    private fun loadMyUserData() {
        viewModelScope.launch {
            val sessionResult = userUseCases.getCurrentUserId()
            if (sessionResult is DataResourceResult.Success) {
                _currentUserId.value = sessionResult.data
            }

            userUseCases.getMyProfile().collect { result ->
                if (result is DataResourceResult.Success) {
                    _myLikedIds.value = result.data.likedPostIds.toSet()
                    _myBookmarkedIds.value = result.data.bookmarkedPostIds.toSet()
                    _currentUserProfileUrl.value = result.data.profileImageUrl
                    if (result.data.id.isNotEmpty()) _currentUserId.value = result.data.id
                    refreshAllLists()
                }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            launch {
                postUseCases.getPopularPosts().collect { result ->
                    if (result is DataResourceResult.Success) {
                        _trendingPosts.value = mapWithMyState(result.data.map { it.toUiModel() })
                        checkLoadingFinished()
                    }
                }
            }
            launch {
                postUseCases.getMostBookmarkedPosts().collect { result ->
                    if (result is DataResourceResult.Success) {
                        _bookmarkedPosts.value = mapWithMyState(result.data.map { it.toUiModel() })
                    }
                }
            }
            launch {
                postUseCases.getRecommendedMasters().collect { result ->
                    if (result is DataResourceResult.Success) {
                        _teaMasters.value = result.data.map { it.toUiModel() }
                    }
                }
            }
            observeFollowingFeed()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeFollowingFeed() {
        val idResult = userUseCases.getCurrentUserId()
        val myId = if (idResult is DataResourceResult.Success) idResult.data else ""

        if (myId.isNotEmpty()) {
            userUseCases.getFollowingIdsFlow(myId)
                .map { if (it is DataResourceResult.Success) it.data else emptyList() }
                .distinctUntilChanged()
                .flatMapLatest { ids -> postUseCases.getFollowingFeed(ids) }
                .onEach { result ->
                    if (result is DataResourceResult.Success) {
                        _followingPosts.value = mapWithMyState(result.data.map { it.toUiModel() })
                        checkLoadingFinished()
                    }
                }.launchIn(viewModelScope)
        } else {
            checkLoadingFinished()
        }
    }

    private fun mapWithMyState(posts: List<CommunityPostUiModel>): List<CommunityPostUiModel> {
        val likes = _myLikedIds.value
        val bookmarks = _myBookmarkedIds.value
        return posts.map { post ->
            val isLiked = likes.contains(post.postId)
            val isBookmarked = bookmarks.contains(post.postId)
            post.updateLike(isLiked, post.likeCount)
                .updateBookmark(isBookmarked, post.bookmarkCount)
        }
    }

    private fun refreshAllLists() {
        _trendingPosts.update { mapWithMyState(it) }
        _bookmarkedPosts.update { mapWithMyState(it) }
        _followingPosts.update { mapWithMyState(it) }
    }

    private fun checkLoadingFinished() {
        _uiControlState.update { it.copy(isInitialLoading = false) }
    }

    val uiState: StateFlow<CommunityUiState> = combine(
        _selectedTab, _uiControlState, _trendingPosts, _bookmarkedPosts,
        _followingPosts, _teaMasters, _currentUserProfileUrl, _currentUserId
    ) { tab, uiControl, trending, bookmarked, following, masters, myProfileUrl, myId ->

        val hasDataToShow = when(tab) {
            CommunityTab.TRENDING -> trending.isNotEmpty() || bookmarked.isNotEmpty()
            CommunityTab.FOLLOWING -> following.isNotEmpty()
        }

        CommunityUiState(
            selectedTab = tab,
            isLoading = uiControl.isInitialLoading && !hasDataToShow,
            errorMessage = uiControl.errorMessage,
            currentUserId = myId,
            popularPosts = trending,
            mostBookmarkedPosts = bookmarked,
            teaMasters = masters,
            followingPosts = following,
            isFollowingEmpty = following.isEmpty() && !uiControl.isInitialLoading,
            currentUserProfileUrl = myProfileUrl,
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

    fun onTabSelected(tab: CommunityTab) { _selectedTab.value = tab }

    fun refresh() {
        _uiControlState.update { it.copy(errorMessage = null, isInitialLoading = true) }
        loadInitialData()
    }

    fun toggleLike(postId: String) {
        val isCurrentlyLiked = _myLikedIds.value.contains(postId)
        val newLiked = !isCurrentlyLiked

        if (newLiked) _myLikedIds.update { it + postId } else _myLikedIds.update { it - postId }
        updatePostCountsAndState(postId, isLikeToggle = true, isAdd = newLiked)

        viewModelScope.launch {
            val result = postUseCases.toggleLike(postId)
            if (result is DataResourceResult.Failure) {
                if (newLiked) _myLikedIds.update { it - postId } else _myLikedIds.update { it + postId }
                updatePostCountsAndState(postId, isLikeToggle = true, isAdd = !newLiked)
                sendEffect(CommunityFeedSideEffect.ShowSnackbar(
                    UiText.DynamicString("좋아요 반영에 실패했습니다.")
                ))
            }
        }
    }

    fun toggleBookmark(postId: String) {
        val isCurrentlyBookmarked = _myBookmarkedIds.value.contains(postId)
        val newBookmarked = !isCurrentlyBookmarked

        if (newBookmarked) _myBookmarkedIds.update { it + postId } else _myBookmarkedIds.update { it - postId }
        updatePostCountsAndState(postId, isLikeToggle = false, isAdd = newBookmarked)

        viewModelScope.launch {
            val result = postUseCases.toggleBookmark(postId)
            if (result is DataResourceResult.Failure) {
                if (newBookmarked) _myBookmarkedIds.update { it - postId } else _myBookmarkedIds.update { it + postId }
                updatePostCountsAndState(postId, isLikeToggle = false, isAdd = !newBookmarked)
                sendEffect(CommunityFeedSideEffect.ShowSnackbar(
                    UiText.DynamicString("북마크 저장에 실패했습니다.")
                ))
            } else {
                if (newBookmarked)sendEffect(CommunityFeedSideEffect.ShowSnackbar(
                    UiText.DynamicString("북마크 저장")
                ))
            }
        }
    }

    fun toggleFollow(targetUserId: String) {
        val currentList = _teaMasters.value
        val targetUser = currentList.find { it.userId == targetUserId } ?: return
        val nextState = !targetUser.isFollowing

        _teaMasters.update { list -> list.map { if (it.userId == targetUserId) it.copy(isFollowing = nextState) else it } }

        viewModelScope.launch {
            val result = userUseCases.followUser(targetUserId, nextState)
            if (result is DataResourceResult.Failure) {
                _teaMasters.update { list -> list.map { if (it.userId == targetUserId) it.copy(isFollowing = !nextState) else it } }
                sendEffect(CommunityFeedSideEffect.ShowSnackbar(
                    UiText.DynamicString("팔로우 실패")
                ))
            } else {
                val msg = if (nextState) "팔로우했습니다." else "팔로우를 취소했습니다."
                sendEffect(CommunityFeedSideEffect.ShowSnackbar(
                    UiText.DynamicString(msg)
                ))
            }
        }
    }

    private fun observePostChanges() {
        postUseCases.observePostChanges()
            .onEach { event ->
                when (event) {
                    is PostChangeEvent.Like -> {
                        updatePostCountsAndState(event.postId, isLikeToggle = true, isAdd = event.isLiked)
                    }
                    is PostChangeEvent.Bookmark -> {
                        updatePostCountsAndState(event.postId, isLikeToggle = false, isAdd = event.isBookmarked)
                    }
                }
            }.launchIn(viewModelScope)
    }

    private var commentJob: Job? = null

    fun updateCommentInput(input: String) {
        _uiControlState.update { it.copy(commentInput = input) }
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
                    _uiControlState.update { it.copy(comments = uiModels, isCommentLoading = false) }
                }
                is DataResourceResult.Failure -> {
                    _uiControlState.update { it.copy(isCommentLoading = false, errorMessage = "댓글 로드 실패") }
                }
                else -> {}
            }
        }.launchIn(viewModelScope)
    }

    fun onMessageShown() {
        _uiControlState.update { it.copy(errorMessage = null) }
    }

    private fun sendEffect(effect: CommunityFeedSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }

    private fun updatePostCountsAndState(postId: String, isLikeToggle: Boolean, isAdd: Boolean) {
        val transform: (CommunityPostUiModel) -> CommunityPostUiModel = { post ->
            if (isLikeToggle) {
                val current = post.likeCount.toIntOrNull() ?: 0
                val newCount = (if (isAdd) current + 1 else maxOf(0, current - 1)).toString()
                post.updateLike(isAdd, newCount)
            } else {
                val current = post.bookmarkCount.toIntOrNull() ?: 0
                val newCount = (if (isAdd) current + 1 else maxOf(0, current - 1)).toString()
                post.updateBookmark(isAdd, newCount)
            }
        }
        _trendingPosts.update { list -> list.map { if (it.postId == postId) transform(it) else it } }
        _bookmarkedPosts.update { list -> list.map { if (it.postId == postId) transform(it) else it } }
        _followingPosts.update { list -> list.map { if (it.postId == postId) transform(it) else it } }
    }
}

private fun CommunityPostUiModel.updateLike(isLiked: Boolean, newCount: String): CommunityPostUiModel {
    return when (this) {
        is CommunityPostUiModel.BrewingNote -> this.copy(isLiked = isLiked, likeCount = newCount)
        is CommunityPostUiModel.General -> this.copy(isLiked = isLiked, likeCount = newCount)
    }
}

private fun CommunityPostUiModel.updateBookmark(isBookmarked: Boolean, newCount: String): CommunityPostUiModel {
    return when (this) {
        is CommunityPostUiModel.BrewingNote -> this.copy(isBookmarked = isBookmarked, bookmarkCount = newCount)
        is CommunityPostUiModel.General -> this.copy(isBookmarked = isBookmarked, bookmarkCount = newCount)
    }
}

@Suppress("UNCHECKED_CAST")
fun <T1, T2, T3, T4, T5, T6, T7, T8, R> combine(
    flow1: Flow<T1>, flow2: Flow<T2>, flow3: Flow<T3>, flow4: Flow<T4>,
    flow5: Flow<T5>, flow6: Flow<T6>, flow7: Flow<T7>, flow8: Flow<T8>,
    transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8) -> R
): Flow<R> = combine(listOf(flow1, flow2, flow3, flow4, flow5, flow6, flow7, flow8)) { args ->
    transform(
        args[0] as T1, args[1] as T2, args[2] as T3, args[3] as T4,
        args[4] as T5, args[5] as T6, args[6] as T7, args[7] as T8
    )
}