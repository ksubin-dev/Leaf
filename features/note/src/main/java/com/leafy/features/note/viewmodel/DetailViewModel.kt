package com.leafy.features.note.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.repository.PostChangeEvent
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DetailViewModel(
    private val noteUseCases: NoteUseCases,
    private val userUseCases: UserUseCases,
    private val postUseCases: PostUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage = _userMessage.asSharedFlow()

    private var currentNoteId: String? = null

    init {
        observePostChanges()
    }

    fun loadNote(noteId: String) {
        currentNoteId = noteId

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val myId = (userUseCases.getCurrentUserId() as? DataResourceResult.Success)?.data

            when (val result = noteUseCases.getNoteDetail(noteId)) {
                is DataResourceResult.Success -> {
                    val note = result.data
                    val isAuthor = (myId == note.ownerId)

                    if (!isAuthor && !note.isPublic) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "비공개 처리된 노트입니다.") }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                note = note,
                                isAuthor = isAuthor,
                                isLiked = note.myState.isLiked,
                                isBookmarked = note.myState.isBookmarked
                            )
                        }
                    }
                }
                is DataResourceResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "노트를 불러오지 못했습니다.") }
                }
                is DataResourceResult.Loading -> { /* Handle generic loading if needed */ }
            }
        }
    }

    fun retry() {
        currentNoteId?.let { loadNote(it) }
    }

    fun toggleLike() {
        val currentNote = uiState.value.note ?: return

        val newLiked = !uiState.value.isLiked
        val updatedNote = currentNote.updateLikeState(isLiked = newLiked)

        updateUiStateWithNote(updatedNote)

        viewModelScope.launch {
            val result = postUseCases.toggleLike(currentNote.id)
            if (result is DataResourceResult.Failure) {
                // 실패 시 롤백 (원래 노트로 복구)
                updateUiStateWithNote(currentNote)
                _uiState.update { it.copy(errorMessage = "좋아요 처리에 실패했습니다.") }
            }
        }
    }

    fun toggleBookmark() {
        val currentNote = uiState.value.note ?: return

        val newBookmarked = !uiState.value.isBookmarked
        val updatedNote = currentNote.updateBookmarkState(isBookmarked = newBookmarked)

        updateUiStateWithNote(updatedNote)

        viewModelScope.launch {
            val result = postUseCases.toggleBookmark(currentNote.id)
            if (result is DataResourceResult.Failure) {
                updateUiStateWithNote(currentNote)
                _uiState.update { it.copy(errorMessage = "북마크 처리에 실패했습니다.") }
            } else {
                if (newBookmarked) _userMessage.emit("북마크에 저장되었습니다.")
            }
        }
    }

    fun deleteNote() {
        val noteId = uiState.value.note?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (noteUseCases.deleteNote(noteId)) {
                is DataResourceResult.Success -> {
                    _uiState.update { it.copy(isLoading = false, isDeleteSuccess = true) }
                }
                is DataResourceResult.Failure -> {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "삭제에 실패했습니다.") }
                }
                else -> {}
            }
        }
    }

    fun userMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }


    private fun observePostChanges() {
        postUseCases.postChangeFlow
            .filter { it.postId == uiState.value.note?.id }
            .onEach { event ->
                val currentNote = uiState.value.note ?: return@onEach

                val updatedNote = when (event) {
                    is PostChangeEvent.Like -> currentNote.syncLikeState(event.isLiked)
                    is PostChangeEvent.Bookmark -> currentNote.syncBookmarkState(event.isBookmarked)
                }

                updateUiStateWithNote(updatedNote)
            }
            .launchIn(viewModelScope)
    }

    private fun updateUiStateWithNote(note: BrewingNote) {
        _uiState.update { state ->
            state.copy(
                note = note,
                isLiked = note.myState.isLiked,
                isBookmarked = note.myState.isBookmarked
            )
        }
    }
}

private fun BrewingNote.updateLikeState(isLiked: Boolean): BrewingNote {
    val currentCount = this.stats.likeCount
    val newCount = if (isLiked) currentCount + 1 else maxOf(0, currentCount - 1)

    return this.copy(
        stats = this.stats.copy(likeCount = newCount),
        myState = this.myState.copy(isLiked = isLiked)
    )
}

private fun BrewingNote.updateBookmarkState(isBookmarked: Boolean): BrewingNote {
    val currentCount = this.stats.bookmarkCount
    val newCount = if (isBookmarked) currentCount + 1 else maxOf(0, currentCount - 1)

    return this.copy(
        stats = this.stats.copy(bookmarkCount = newCount),
        myState = this.myState.copy(isBookmarked = isBookmarked)
    )
}
private fun BrewingNote.syncLikeState(targetState: Boolean): BrewingNote {
    if (this.myState.isLiked == targetState) return this

    return this.updateLikeState(targetState)
}

private fun BrewingNote.syncBookmarkState(targetState: Boolean): BrewingNote {
    if (this.myState.isBookmarked == targetState) return this

    return this.updateBookmarkState(targetState)
}