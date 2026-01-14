package com.leafy.features.note.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.PostUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val noteUseCases: NoteUseCases,
    private val userUseCases: UserUseCases,
    private val postUseCases: PostUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var currentNoteId: String? = null

    fun loadNote(noteId: String) {

        currentNoteId = noteId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val myIdResult = userUseCases.getCurrentUserId()
            val myId = if (myIdResult is DataResourceResult.Success) myIdResult.data else null

            when (val result = noteUseCases.getNoteDetail(noteId)) {
                is DataResourceResult.Success -> {
                    val note = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            note = note,
                            isAuthor = (myId != null && myId == note.ownerId),
                            isLiked = note.myState.isLiked,
                            isBookmarked = note.myState.isBookmarked
                        )
                    }
                }
                is DataResourceResult.Failure -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "노트를 불러오지 못했습니다."
                        )
                    }
                }

                is DataResourceResult.Loading ->{

                }
            }
        }
    }

    fun retry() {
        currentNoteId?.let { id ->
            loadNote(id)
        }
    }

    fun toggleLike() {
        val noteId = uiState.value.note?.id ?: return

        _uiState.update { it.copy(isLiked = !it.isLiked) }

        viewModelScope.launch {
            val result = postUseCases.toggleLike(noteId)
            if (result is DataResourceResult.Failure) {
                _uiState.update { it.copy(isLiked = !it.isLiked) }
            }
        }
    }

    fun toggleBookmark() {
        val noteId = uiState.value.note?.id ?: return

        _uiState.update { it.copy(isBookmarked = !it.isBookmarked) }

        viewModelScope.launch {
            val result = postUseCases.toggleBookmark(noteId)
            if (result is DataResourceResult.Failure) {
                _uiState.update { it.copy(isBookmarked = !it.isBookmarked) }
            }
        }
    }


    fun deleteNote() {
        val noteId = uiState.value.note?.id ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = noteUseCases.deleteNote(noteId)

            if (result is DataResourceResult.Success) {
                _uiState.update { it.copy(isLoading = false, isDeleteSuccess = true) }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "삭제에 실패했습니다.")
                }
            }
        }
    }

    fun userMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}