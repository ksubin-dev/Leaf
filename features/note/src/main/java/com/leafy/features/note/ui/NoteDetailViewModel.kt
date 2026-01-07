package com.leafy.features.note.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.usecase.CommunityUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface NoteDetailUiEffect {
    data class ShowSnackbar(val message: String) : NoteDetailUiEffect
    object NavigateBack : NoteDetailUiEffect
}

class NoteDetailViewModel(
    private val noteUseCases: NoteUseCases,
    private val communityUseCases: CommunityUseCases,
    private val noteId: String
) : ViewModel() {

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _effect = MutableSharedFlow<NoteDetailUiEffect>()
    val effect = _effect.asSharedFlow()

    val uiState: StateFlow<NoteUiState> = combine(
        noteUseCases.getNoteById(noteId),
        _isProcessing
    ) { result, processing ->
        when (result) {
            is DataResourceResult.Loading -> NoteUiState(isLoading = true)
            is DataResourceResult.Success -> {
                result.data.toUiState().copy(isLoading = processing)
            }
            is DataResourceResult.Failure -> {
                NoteUiState(isLoading = false, errorMessage = "데이터 로드 실패: ${result.exception.message}")
            }
            else -> NoteUiState(isLoading = false)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NoteUiState(isLoading = true)
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val isAuthor: StateFlow<Boolean> = flow {
        emit(noteUseCases.getCurrentUserId())
    }.flatMapLatest { currentUserId ->
        uiState.map { state ->
            currentUserId != null && state.ownerId == currentUserId
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // --- 기능 함수 (이벤트 처리) ---

    fun toggleLike(currentStatus: Boolean) {
        viewModelScope.launch {
            communityUseCases.toggleLike(noteId, currentStatus)
        }
    }

    fun toggleSave(currentStatus: Boolean) {
        viewModelScope.launch {
            communityUseCases.toggleSave(noteId, currentStatus)
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            val currentUserId = noteUseCases.getCurrentUserId() ?: return@launch
            val currentState = uiState.value
            if (currentState.ownerId != currentUserId) {
                _effect.emit(NoteDetailUiEffect.ShowSnackbar("삭제 권한이 없습니다."))
                return@launch
            }

            _isProcessing.value = true
            noteUseCases.deleteNote(
                currentUserId = currentUserId,
                noteId = noteId,
                ownerId = currentState.ownerId
            ).collect { result ->
                when (result) {
                    is DataResourceResult.Success -> {
                        _isProcessing.value = false
                        _effect.emit(NoteDetailUiEffect.ShowSnackbar("삭제되었습니다."))
                        _effect.emit(NoteDetailUiEffect.NavigateBack)
                    }
                    is DataResourceResult.Failure -> {
                        _isProcessing.value = false
                        _effect.emit(NoteDetailUiEffect.ShowSnackbar("오류: ${result.exception.message}"))
                    }
                    else -> Unit
                }
            }
        }
    }
}