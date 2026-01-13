package com.leafy.features.note.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val noteUseCases: NoteUseCases,
    private val userUseCases: UserUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    // 화면 진입 시 노트 ID로 데이터 로드
    fun loadNote(noteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. 내 ID 가져오기
            val myIdResult = userUseCases.getCurrentUserId()
            val myId = if (myIdResult is DataResourceResult.Success) myIdResult.data else null

            // 2. 노트 정보 가져오기
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
                        it.copy(isLoading = false, errorMessage = "노트를 불러오지 못했습니다.")
                    }
                }

                else -> {}
            }
        }
    }

    // 좋아요 토글
    fun toggleLike() {
        val noteId = uiState.value.note?.id ?: return
        // UI 낙관적 업데이트 (서버 응답 기다리지 않고 즉시 반영)
        _uiState.update { it.copy(isLiked = !it.isLiked) }

        viewModelScope.launch {
            // TODO: noteUseCases.likeNote(noteId) 같은 기능 연결
            // 실패 시 롤백 로직 필요
        }
    }

    // 북마크 토글
    fun toggleBookmark() {
        val noteId = uiState.value.note?.id ?: return
        _uiState.update { it.copy(isBookmarked = !it.isBookmarked) }

        viewModelScope.launch {
            // TODO: noteUseCases.bookmarkNote(noteId) 연결
        }
    }

    // 노트 삭제
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

    // 에러 메시지 확인
    fun userMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}