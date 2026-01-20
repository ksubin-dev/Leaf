package com.leafy.features.community.presentation.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.ui.mapper.toUiModel
import com.leafy.shared.ui.model.CommentUiModel
import com.leafy.shared.ui.model.CommunityPostUiModel
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.repository.PostChangeEvent
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CommunityPostDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val postUseCases: PostUseCases,
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val postId: String = savedStateHandle.get<String>("postId")
        ?: throw IllegalArgumentException("Post ID is required")

    private val _uiState = MutableStateFlow(CommunityPostDetailUiState())
    val uiState = _uiState.asStateFlow()

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
            _uiState.update { it.copy(isLoading = true) }
            fetchPostDetail()
            fetchCurrentUserProfile()
            _uiState.update { it.copy(isLoading = false) }
        }

        viewModelScope.launch {
            fetchComments()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val result = postUseCases.getPostDetail(postId)
            when (result) {
                is DataResourceResult.Success -> {
                    _uiState.update { it.copy(post = result.data.toUiModel()) }
                    fetchComments()
                }
                is DataResourceResult.Failure -> {
                    _uiState.update {
                        it.copy(post = null, errorMessage = "삭제된 게시글입니다.")
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun fetchPostDetail() {
        val result = postUseCases.getPostDetail(postId)
        if (result is DataResourceResult.Success) {
            _uiState.update { it.copy(post = result.data.toUiModel()) }
        } else if (result is DataResourceResult.Failure) {
            _uiState.update { it.copy(errorMessage = "게시글을 불러올 수 없습니다.") }
        }
    }

    private suspend fun fetchComments() {
        postUseCases.getComments(postId).collect { result ->
            if (result is DataResourceResult.Success) {
                _uiState.update { state ->
                    state.copy(
                        comments = result.data.map { comment -> comment.toUiModel() }
                    )
                }
            }
        }
    }

    private suspend fun fetchCurrentUserProfile() {
        val idResult = userUseCases.getCurrentUserId()
        if (idResult is DataResourceResult.Success) {
            val myId = idResult.data
            val profileResult = userUseCases.getUserProfile(myId)
            if (profileResult is DataResourceResult.Success) {
                _uiState.update {
                    it.copy(currentUserProfileUrl = profileResult.data.profileImageUrl)
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
            val result = postUseCases.addComment(postId, content)
            if (result is DataResourceResult.Success) {
                _uiState.update { it.copy(commentInput = "") }
                fetchPostDetail()
            }
            _uiState.update { it.copy(isSendingComment = false) }
        }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            val result = postUseCases.deleteComment(postId, commentId)
            if (result is DataResourceResult.Success) {
                fetchPostDetail()
            }
        }
    }

    fun toggleLike() {
        val currentPost = uiState.value.post ?: return
        val newLiked = !currentPost.isLiked
        val updatedPost = currentPost.updateLikeState(newLiked)

        _uiState.update { it.copy(post = updatedPost) }

        viewModelScope.launch {
            val result = postUseCases.toggleLike(postId)
            if (result is DataResourceResult.Failure) {
                _uiState.update { it.copy(post = currentPost) }
            }
        }
    }

    fun toggleBookmark() {
        val currentPost = uiState.value.post ?: return
        val newBookmarked = !currentPost.isBookmarked
        val updatedPost = currentPost.updateBookmarkState(newBookmarked)

        _uiState.update { it.copy(post = updatedPost) }

        viewModelScope.launch {
            val result = postUseCases.toggleBookmark(postId)
            if (result is DataResourceResult.Failure) {
                _uiState.update { it.copy(post = currentPost) }
            }
        }
    }

    private fun observePostChanges() {
        postUseCases.postChangeFlow
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

data class CommunityPostDetailUiState(
    val isLoading: Boolean = false,
    val post: CommunityPostUiModel? = null,
    val comments: List<CommentUiModel> = emptyList(),
    val commentInput: String = "",
    val isSendingComment: Boolean = false,
    val currentUserProfileUrl: String? = null,
    val errorMessage: String? = null
)