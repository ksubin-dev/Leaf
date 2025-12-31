package com.leafy.features.note.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.NoteUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface NoteDetailUiEffect {
    data class ShowSnackbar(val message: String) : NoteDetailUiEffect
    object NavigateBack : NoteDetailUiEffect
}

class NoteDetailViewModel(
    private val noteUseCases: NoteUseCases,
    private val noteId: String
) : ViewModel() {

    private val userId: StateFlow<String?> = flow {
        emit(noteUseCases.getCurrentUserId())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState = _uiState.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _effect = MutableSharedFlow<NoteDetailUiEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadNoteDetail()
    }

    private fun loadNoteDetail() {
        viewModelScope.launch {
            val currentId = userId.value ?: noteUseCases.getCurrentUserId() ?: run {
                _effect.emit(NoteDetailUiEffect.ShowSnackbar("로그인이 필요합니다."))
                return@launch
            }

            noteUseCases.getNoteById(currentId, noteId).collectLatest { result ->
                when (result) {
                    is DataResourceResult.Loading -> _isProcessing.update { true }
                    is DataResourceResult.Success -> {
                        _isProcessing.update { false }
                        _uiState.update { result.data.toUiState() }
                    }
                    is DataResourceResult.Failure -> {
                        _isProcessing.update { false }
                        _effect.emit(NoteDetailUiEffect.ShowSnackbar("데이터를 불러오지 못했습니다."))
                    }
                    else -> _isProcessing.update { false }
                }
            }
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            val currentUserId = userId.value ?: return@launch
            val noteToDelete = uiState.value.toDomain(currentUserId, noteId)

            noteUseCases.deleteNote(currentUserId, noteToDelete).collectLatest { result ->
                when (result) {
                    is DataResourceResult.Loading -> _isProcessing.update { true }
                    is DataResourceResult.Success -> {
                        _isProcessing.update { false }
                        _effect.emit(NoteDetailUiEffect.ShowSnackbar("삭제되었습니다."))
                        _effect.emit(NoteDetailUiEffect.NavigateBack)
                    }
                    is DataResourceResult.Failure -> {
                        _isProcessing.update { false }
                        _effect.emit(NoteDetailUiEffect.ShowSnackbar("삭제 실패: ${result.exception.message}"))
                    }
                    else -> _isProcessing.update { false }
                }
            }
        }
    }
}