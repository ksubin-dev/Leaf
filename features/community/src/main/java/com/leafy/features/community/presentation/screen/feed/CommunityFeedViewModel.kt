package com.leafy.features.community.presentation.screen.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.ui.mapper.toUiModel
import com.leafy.shared.ui.model.CommentUiModel
import com.leafy.shared.ui.model.CommunityPostUiModel
import com.leafy.shared.ui.model.UserUiModel
import com.leafy.features.community.presentation.components.bar.CommunityTab
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.PostChangeEvent
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface CommunityFeedSideEffect {
    object HideKeyboard : CommunityFeedSideEffect
}

@OptIn(ExperimentalCoroutinesApi::class)
class CommunityFeedViewModel(
    private val postUseCases: PostUseCases,
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _sideEffects = Channel<CommunityFeedSideEffect>()
    val sideEffects = _sideEffects.receiveAsFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage = _userMessage.asSharedFlow()

    // 탭 상태
    private val _selectedTab = MutableStateFlow(CommunityTab.TRENDING)

    private val _trendingPosts = MutableStateFlow<List<CommunityPostUiModel>>(emptyList())
    private val _bookmarkedPosts = MutableStateFlow<List<CommunityPostUiModel>>(emptyList())
    private val _followingPosts = MutableStateFlow<List<CommunityPostUiModel>>(emptyList())
    private val _teaMasters = MutableStateFlow<List<UserUiModel>>(emptyList())

    private val _myLikedIds = MutableStateFlow<Set<String>>(emptySet())
    private val _myBookmarkedIds = MutableStateFlow<Set<String>>(emptySet())

    private val _uiControlState = MutableStateFlow(UiControlState())

    private val _currentUserProfileUrl = MutableStateFlow<String?>(null)

    private data class UiControlState(
        val showCommentSheet: Boolean = false,
        val selectedPostIdForComments: String? = null,
        val comments: List<CommentUiModel> = emptyList(),
        val commentInput: String = "",
        val isCommentLoading: Boolean = false,
        val errorMessage: String? = null,
        val isInitialLoading: Boolean = true
    )
    private val _currentUserId = MutableStateFlow<String?>(null)

    init {
        loadMyUserData()
        loadInitialData()
        observePostChanges()
    }

    private fun loadMyUserData() {
        val sessionResult = userUseCases.getCurrentUserId()
        if (sessionResult is DataResourceResult.Success) {
            _currentUserId.value = sessionResult.data
        }

        userUseCases.getMyProfile().onEach { result ->
            if (result is DataResourceResult.Success) {
                _myLikedIds.value = result.data.likedPostIds.toSet()
                _myBookmarkedIds.value = result.data.bookmarkedPostIds.toSet()
                _currentUserProfileUrl.value = result.data.profileImageUrl

                if (result.data.id.isNotEmpty()) {
                    _currentUserId.value = result.data.id
                }

                refreshAllLists()
            }
        }.launchIn(viewModelScope)
    }
    private fun loadInitialData() {
        postUseCases.getPopularPosts().onEach { result ->
            when (result) {
                is DataResourceResult.Success -> {
                    _trendingPosts.value = mapWithMyState(result.data.map { it.toUiModel() })
                    checkLoadingFinished()
                }
                is DataResourceResult.Failure -> {
                    result.exception.printStackTrace()
                }
                else -> {}
            }
        }.launchIn(viewModelScope)

        postUseCases.getMostBookmarkedPosts().onEach { result ->
            if (result is DataResourceResult.Success) {
                _bookmarkedPosts.value = mapWithMyState(result.data.map { it.toUiModel() })
            } else if (result is DataResourceResult.Failure) {
                Log.e("CommunityError", "명예의 전당 로드 실패: ${result.exception.message}")
            }
        }.launchIn(viewModelScope)

        postUseCases.getRecommendedMasters().onEach { result ->
            if (result is DataResourceResult.Success) {
                _teaMasters.value = result.data.map { it.toUiModel() }
            } else if (result is DataResourceResult.Failure) {
                Log.e("CommunityError", "티 마스터 로드 실패: ${result.exception.message}")
            }
        }.launchIn(viewModelScope)

        observeFollowingFeed()
    }

    private fun observeFollowingFeed() {
        val idResult = userUseCases.getCurrentUserId()
        val myId = if (idResult is DataResourceResult.Success) idResult.data else ""

        if (myId.isNotEmpty()) {
            userUseCases.getFollowingIdsFlow(myId)
                .map { result -> if (result is DataResourceResult.Success) result.data else emptyList() }
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
        _selectedTab,
        _uiControlState,
        _trendingPosts,
        _bookmarkedPosts,
        _followingPosts,
        _teaMasters,
        _currentUserProfileUrl,
        _currentUserId
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


    fun onTabSelected(tab: CommunityTab) {
        _selectedTab.value = tab
    }

    fun refresh() {
        _uiControlState.update { it.copy(errorMessage = null, isInitialLoading = true) }
        loadInitialData()
    }

    fun toggleLike(postId: String) {
        val isCurrentlyLiked = _myLikedIds.value.contains(postId)
        val newLiked = !isCurrentlyLiked

        if (newLiked) _myLikedIds.update { it + postId }
        else _myLikedIds.update { it - postId }

        updatePostCountsAndState(postId, isLikeToggle = true, isAdd = newLiked)

        viewModelScope.launch {

            val result = postUseCases.toggleLike(postId)

            if (result is DataResourceResult.Failure) {
                if (newLiked) _myLikedIds.update { it - postId } else _myLikedIds.update { it + postId }
                updatePostCountsAndState(postId, isLikeToggle = true, isAdd = !newLiked) // 반대로 다시 업데이트
                _userMessage.emit("좋아요 반영에 실패했습니다. 다시 시도해주세요.")
            }
        }
    }

    fun toggleBookmark(postId: String) {
        val isCurrentlyBookmarked = _myBookmarkedIds.value.contains(postId)
        val newBookmarked = !isCurrentlyBookmarked

        if (newBookmarked) _myBookmarkedIds.update { it + postId }
        else _myBookmarkedIds.update { it - postId }

        updatePostCountsAndState(postId, isLikeToggle = false, isAdd = newBookmarked)

        viewModelScope.launch {
            val result = postUseCases.toggleBookmark(postId)

            if (result is DataResourceResult.Failure) {
                if (newBookmarked) _myBookmarkedIds.update { it - postId } else _myBookmarkedIds.update { it + postId }
                updatePostCountsAndState(postId, isLikeToggle = false, isAdd = !newBookmarked)
                _userMessage.emit("북마크 저장에 실패했습니다.")
            } else {
                if (newBookmarked) {
                    _userMessage.emit("북마크 저장되었습니다.")
                }
            }
        }
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
    fun toggleFollow(targetUserId: String) {
        val currentList = _teaMasters.value
        val targetUser = currentList.find { it.userId == targetUserId } ?: return
        val nextState = !targetUser.isFollowing

        _teaMasters.update { list ->
            list.map { if (it.userId == targetUserId) it.copy(isFollowing = nextState) else it }
        }

        viewModelScope.launch {
            val result = userUseCases.followUser(targetUserId, nextState)
            if (result is DataResourceResult.Failure) {
                _teaMasters.update { list ->
                    list.map { if (it.userId == targetUserId) it.copy(isFollowing = !nextState) else it }
                }
                _userMessage.emit("요청 처리에 실패했습니다.")
            } else {
                if (nextState) _userMessage.emit("팔로우했습니다.")
                else _userMessage.emit("팔로우를 취소했습니다.")
            }
        }
    }

    private fun observePostChanges() {
        postUseCases.postChangeFlow
            .onEach { event ->
                when (event) {
                    is PostChangeEvent.Like -> {
                        updatePostCountsAndState(
                            postId = event.postId,
                            isLikeToggle = true,
                            isAdd = event.isLiked
                        )
                    }
                    is PostChangeEvent.Bookmark -> {
                        updatePostCountsAndState(
                            postId = event.postId,
                            isLikeToggle = false,
                            isAdd = event.isBookmarked
                        )
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

//    fun addComment() {
//        val postId = _uiControlState.value.selectedPostIdForComments ?: return
//        val content = _uiControlState.value.commentInput
//        if (content.isBlank()) return
//
//        viewModelScope.launch {
//            _uiControlState.update { it.copy(isCommentLoading = true) }
//            when (val result = postUseCases.addComment(postId, content)) {
//                is DataResourceResult.Success -> {
//                    _uiControlState.update { it.copy(commentInput = "", isCommentLoading = false) }
//                    _sideEffects.send(CommunityFeedSideEffect.HideKeyboard)
//                }
//                is DataResourceResult.Failure -> {
//                    _uiControlState.update { it.copy(errorMessage = "댓글 등록 실패", isCommentLoading = false) }
//                }
//                else -> {}
//            }
//        }
//    }
//
//    fun deleteComment(commentId: String) {
//        val postId = _uiControlState.value.selectedPostIdForComments ?: return
//        viewModelScope.launch {
//            postUseCases.deleteComment(postId, commentId)
//        }
//    }

    fun onMessageShown() {
        _uiControlState.update { it.copy(errorMessage = null) }
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