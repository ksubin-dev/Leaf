package com.leafy.features.note.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.NoteUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// NoteViewModel과 동일한 Effect 구조 사용
sealed interface NoteDetailUiEffect {
    data class ShowToast(val message: String) : NoteDetailUiEffect
    object NavigateBack : NoteDetailUiEffect
}

class NoteDetailViewModel(
    private val noteUseCases: NoteUseCases,
    private val noteId: String
) : ViewModel() {

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
            noteUseCases.getNoteById(noteId).collectLatest { result ->
                when (result) {
                    is DataResourceResult.Loading -> {
                        _isProcessing.update { true }
                    }
                    is DataResourceResult.Success -> {
                        _isProcessing.update { false }
                        // 🎯 NoteUiMapper에 정의된 toUiState()를 호출합니다.
                        _uiState.update { result.data.toUiState() }
                    }
                    is DataResourceResult.Failure -> {
                        _isProcessing.update { false }
                        _effect.emit(NoteDetailUiEffect.ShowToast("데이터를 불러오지 못했습니다."))
                    }
                    else -> _isProcessing.update { false }
                }
            }
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            noteUseCases.deleteNote(noteId).collectLatest { result ->
                when (result) {
                    is DataResourceResult.Loading -> _isProcessing.update { true }
                    is DataResourceResult.Success -> {
                        _isProcessing.update { false }
                        _effect.emit(NoteDetailUiEffect.ShowToast("노트가 삭제되었습니다."))
                        _effect.emit(NoteDetailUiEffect.NavigateBack)
                    }
                    is DataResourceResult.Failure -> {
                        _isProcessing.update { false }
                        _effect.emit(NoteDetailUiEffect.ShowToast("삭제 실패: ${result.exception.message}"))
                    }
                    else -> _isProcessing.update { false }
                }
            }
        }
    }
}
