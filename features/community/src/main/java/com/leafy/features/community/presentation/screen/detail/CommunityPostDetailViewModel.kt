package com.leafy.features.community.presentation.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.R
import com.leafy.shared.ui.mapper.toUiModel
import com.leafy.shared.ui.model.CommentUiModel
import com.leafy.shared.ui.model.CommunityPostUiModel
import com.leafy.shared.utils.UiText
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.PostChangeEvent
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PostDetailSideEffect {
    data class ShowToast(val message: UiText) : PostDetailSideEffect
    data object NavigateBack : PostDetailSideEffect
}

data class CommunityPostDetailUiState(
    val isLoading: Boolean = true,
    val post: CommunityPostUiModel? = null,
    val comments: List<CommentUiModel> = emptyList(),
    val commentInput: String = "",
    val isSendingComment: Boolean = false,
    val currentUserProfileUrl: String? = null,
    val currentUserId: String? = null,
    val errorMessage: UiText? = null
)

@HiltViewModel
class CommunityPostDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postUseCases: PostUseCases,
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val postId: String = checkNotNull(savedStateHandle["postId"]) { "Post ID is required" }

    private val _uiState = MutableStateFlow(CommunityPostDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val _sideEffect = Channel<PostDetailSideEffect>()
    val sideEffect: Flow<PostDetailSideEffect> = _sideEffect.receiveAsFlow()

    init {
        incrementViewCount()
        loadData()
        observePostChanges()
    }

    private fun incrementViewCount() {
        viewModelScope.launch {
            postUseCases.incrementViewCount(postId)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            launch { fetchCurrentUserProfile() }
            fetchPostDetail()
            observeComments()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            fetchPostDetail()
        }
    }

    private suspend fun fetchPostDetail() {
        _uiState.update { it.copy(isLoading = true) }

        when (val result = postUseCases.getPostDetail(postId)) {
            is DataResourceResult.Success -> {
                _uiState.update {
                    it.copy(post = result.data.toUiModel(), isLoading = false)
                }
            }
            is DataResourceResult.Failure -> {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = UiText.StringResource(R.string.msg_post_load_error))
                }
                sendEffect(PostDetailSideEffect.ShowToast(UiText.StringResource(R.string.msg_post_deleted)))
                sendEffect(PostDetailSideEffect.NavigateBack)
            }
            else -> { _uiState.update { it.copy(isLoading = false) } }
        }
    }

    private suspend fun observeComments() {
        postUseCases.getComments(postId).collect { result ->
            when (result) {
                is DataResourceResult.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            comments = result.data.map { it.toUiModel() }
                        )
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun fetchCurrentUserProfile() {
        when (val idResult = userUseCases.getCurrentUserId()) {
            is DataResourceResult.Success -> {
                val myId = idResult.data
                _uiState.update { it.copy(currentUserId = myId) }

                when (val profileResult = userUseCases.getUserProfile(myId)) {
                    is DataResourceResult.Success -> {
                        _uiState.update {
                            it.copy(currentUserProfileUrl = profileResult.data.profileImageUrl)
                        }
                    }
                    else -> {}
                }
            }
            else -> {}
        }
    }

    fun deletePost() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = postUseCases.deletePost(postId)) {
                is DataResourceResult.Success -> {
                    sendEffect(PostDetailSideEffect.ShowToast(UiText.StringResource(R.string.msg_post_deleted)))
                    sendEffect(PostDetailSideEffect.NavigateBack)
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                    sendEffect(PostDetailSideEffect.ShowToast(UiText.StringResource(R.string.msg_delete_failed)))
                }
            }
        }
    }

    fun updateCommentInput(text: String) {
        _uiState.update { it.copy(commentInput = text) }
    }

    fun sendComment() {
        val content = uiState.value.commentInput
        if (content.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSendingComment = true) }

            when (val result = postUseCases.addComment(postId, content)) {
                is DataResourceResult.Success -> {
                    _uiState.update { it.copy(commentInput = "") }
                    fetchPostDetail()
                }
                else -> {
                    sendEffect(PostDetailSideEffect.ShowToast(UiText.StringResource(R.string.msg_comment_failed)))
                }
            }
            _uiState.update { it.copy(isSendingComment = false) }
        }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            when (val result = postUseCases.deleteComment(postId, commentId)) {
                is DataResourceResult.Success -> {
                    sendEffect(PostDetailSideEffect.ShowToast(UiText.StringResource(R.string.msg_comment_deleted)))
                    fetchPostDetail()
                }
                else -> {
                    sendEffect(PostDetailSideEffect.ShowToast(UiText.StringResource(R.string.msg_delete_failed)))
                }
            }
        }
    }

    fun toggleLike() {
        val currentPost = uiState.value.post ?: return
        val newLiked = !currentPost.isLiked

        val updatedPost = currentPost.updateLikeState(newLiked)
        _uiState.update { it.copy(post = updatedPost) }

        viewModelScope.launch {
            when (val result = postUseCases.toggleLike(postId)) {
                is DataResourceResult.Failure -> {
                    _uiState.update { it.copy(post = currentPost) }
                    sendEffect(PostDetailSideEffect.ShowToast(UiText.StringResource(R.string.msg_like_failed)))
                }
                else -> {}
            }
        }
    }

    fun toggleBookmark() {
        val currentPost = uiState.value.post ?: return
        val newBookmarked = !currentPost.isBookmarked

        val updatedPost = currentPost.updateBookmarkState(newBookmarked)
        _uiState.update { it.copy(post = updatedPost) }

        viewModelScope.launch {
            when (val result = postUseCases.toggleBookmark(postId)) {
                is DataResourceResult.Failure -> {
                    _uiState.update { it.copy(post = currentPost) }
                    sendEffect(PostDetailSideEffect.ShowToast(UiText.StringResource(R.string.msg_bookmark_failed)))
                }
                is DataResourceResult.Success -> {
                    if (newBookmarked) {
                        sendEffect(PostDetailSideEffect.ShowToast(UiText.StringResource(R.string.msg_bookmark_saved)))
                    }
                }
                else -> {}
            }
        }
    }

    private fun observePostChanges() {
        postUseCases.observePostChanges()
            .filter { it.postId == this.postId }
            .onEach { event ->
                val currentPost = uiState.value.post ?: return@onEach
                val updatedPost = when (event) {
                    is PostChangeEvent.Like -> currentPost.syncLikeState(event.isLiked)
                    is PostChangeEvent.Bookmark -> currentPost.syncBookmarkState(event.isBookmarked)
                }
                _uiState.update { it.copy(post = updatedPost) }
            }
            .launchIn(viewModelScope)
    }

    private fun sendEffect(effect: PostDetailSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }
}

private fun CommunityPostUiModel.updateLikeState(isLiked: Boolean): CommunityPostUiModel {
    val currentCount = this.likeCount.toIntOrNull() ?: 0
    val newCount = (if (isLiked) currentCount + 1 else maxOf(0, currentCount - 1)).toString()
    return when (this) {
        is CommunityPostUiModel.General -> this.copy(isLiked = isLiked, likeCount = newCount)
        is CommunityPostUiModel.BrewingNote -> this.copy(isLiked = isLiked, likeCount = newCount)
    }
}

private fun CommunityPostUiModel.updateBookmarkState(isBookmarked: Boolean): CommunityPostUiModel {
    val currentCount = this.bookmarkCount.toIntOrNull() ?: 0
    val newCount = (if (isBookmarked) currentCount + 1 else maxOf(0, currentCount - 1)).toString()
    return when (this) {
        is CommunityPostUiModel.General -> this.copy(isBookmarked = isBookmarked, bookmarkCount = newCount)
        is CommunityPostUiModel.BrewingNote -> this.copy(isBookmarked = isBookmarked, bookmarkCount = newCount)
    }
}

private fun CommunityPostUiModel.syncLikeState(targetState: Boolean): CommunityPostUiModel {
    if (this.isLiked == targetState) return this
    return this.updateLikeState(targetState)
}

private fun CommunityPostUiModel.syncBookmarkState(targetState: Boolean): CommunityPostUiModel {
    if (this.isBookmarked == targetState) return this
    return this.updateBookmarkState(targetState)
}