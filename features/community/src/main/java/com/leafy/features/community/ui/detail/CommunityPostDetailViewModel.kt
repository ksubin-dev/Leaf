package com.leafy.features.community.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.features.community.ui.mapper.toUiModel
import com.leafy.features.community.ui.model.CommentUiModel
import com.leafy.features.community.ui.model.CommunityPostUiModel
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        loadData()
    }

    private fun loadData() {
        // 1. 일반 데이터 로드 (게시글 상세, 내 프로필) -> 로딩바 제어 필요
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            fetchPostDetail()
            fetchCurrentUserProfile() // 내 아이디 찾고 -> 프로필 조회

            _uiState.update { it.copy(isLoading = false) }
        }

        // 2. 댓글 Flow 구독 (별도 코루틴) -> 로딩바와 무관하게 계속 실시간 감시
        viewModelScope.launch {
            fetchComments()
        }
    }

    private suspend fun fetchPostDetail() {
        val result = postUseCases.getPostDetail(postId)
        if (result is DataResourceResult.Success) {
            _uiState.update { it.copy(post = result.data.toUiModel()) }
        }
    }

    // Flow 수집 함수
    private suspend fun fetchComments() {
        // collect는 무한루프처럼 동작하므로 별도 launch 안에서 실행됨
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

    // 🚨 [수정됨] 내 ID를 먼저 얻고 -> 내 프로필을 조회
    private suspend fun fetchCurrentUserProfile() {
        // 1. 내 ID 가져오기
        val idResult = userUseCases.getCurrentUserId()
        if (idResult is DataResourceResult.Success) {
            val myId = idResult.data

            // 2. 내 프로필 가져오기 (getUserProfile에 ID 전달)
            val profileResult = userUseCases.getUserProfile(myId)
            if (profileResult is DataResourceResult.Success) {
                _uiState.update {
                    it.copy(currentUserProfileUrl = profileResult.data.profileImageUrl)
                }
            }
        }
    }

    // --- User Actions ---

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

                // 💡 중요: fetchComments() 호출 불필요!
                // Flow가 연결되어 있으므로 서버/DB가 변하면 알아서 UI가 갱신됩니다.

                // 댓글 수 갱신을 위해 게시글 정보만 다시 불러옴 (선택 사항)
                fetchPostDetail()
            }

            _uiState.update { it.copy(isSendingComment = false) }
        }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            // 🚨 [수정됨] postId도 같이 넘겨야 함 (Repository 스펙에 맞춤)
            val result = postUseCases.deleteComment(postId, commentId)

            if (result is DataResourceResult.Success) {
                // 마찬가지로 fetchComments() 호출 불필요
                fetchPostDetail() // 전체 댓글 수 갱신용
            }
        }
    }

    fun toggleLike() {
        val currentPost = uiState.value.post ?: return
        val newLikedState = !currentPost.isLiked

        _uiState.update { state ->
            state.copy(post = currentPost.copy(isLiked = newLikedState))
        }

        viewModelScope.launch {
            val result = postUseCases.toggleLike(postId)
            if (result is DataResourceResult.Success) {
                fetchPostDetail()
            } else {
                _uiState.update { state -> state.copy(post = currentPost) }
            }
        }
    }

    fun toggleBookmark() {
        val currentPost = uiState.value.post ?: return
        val newBookmarkedState = !currentPost.isBookmarked

        _uiState.update { state ->
            state.copy(post = currentPost.copy(isBookmarked = newBookmarkedState))
        }

        viewModelScope.launch {
            val result = postUseCases.toggleBookmark(postId)
            if (result is DataResourceResult.Success) {
                fetchPostDetail()
            } else {
                _uiState.update { state -> state.copy(post = currentPost) }
            }
        }
    }
}

// UI State 정의
data class CommunityPostDetailUiState(
    val isLoading: Boolean = false,
    val post: CommunityPostUiModel? = null,
    val comments: List<CommentUiModel> = emptyList(),
    val commentInput: String = "",
    val isSendingComment: Boolean = false,
    val currentUserProfileUrl: String? = null,
    val errorMessage: String? = null
)