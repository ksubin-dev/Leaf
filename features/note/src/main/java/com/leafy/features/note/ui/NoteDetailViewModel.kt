package com.leafy.features.note.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.usecase.CommunityUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
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


    private val _noteDataSource = communityUseCases.getNoteDetail(noteId)


    val uiState: StateFlow<NoteUiState> = combine(
        _noteDataSource,
        _isProcessing
    ) { result, processing ->
        when (result) {
            is DataResourceResult.Loading -> {
                NoteUiState(isLoading = true)
            }
            is DataResourceResult.Success -> {
                result.data.toUiState().copy(isLoading = processing)
            }
            is DataResourceResult.Failure -> {
                NoteUiState(isLoading = false, errorMessage = "데이터를 불러오지 못했습니다.")
            }
            else -> NoteUiState(isLoading = false)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NoteUiState(isLoading = true)
    )


    val isAuthor: StateFlow<Boolean> = uiState.map { state ->
        val currentUserId = noteUseCases.getCurrentUserId()
        currentUserId != null && state.ownerId == currentUserId
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // --- 기능 함수 (이벤트 처리) ---

    fun toggleLike(currentStatus: Boolean) {
        viewModelScope.launch {
            communityUseCases.toggleLike(noteId, currentStatus)
            // Repository에서 AuthUser를 업데이트하므로,
            // _noteDataSource가 다시 collect 되면서 UI에 실시간 반영됩니다!
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
            val ownerId = currentState.ownerId

            _isProcessing.value = true

            noteUseCases.deleteNote(
                currentUserId = currentUserId,
                noteId = noteId,
                ownerId = ownerId
            ).collect { result ->
                when (result) {
                    is DataResourceResult.Success -> {
                        _isProcessing.value = false
                        _effect.emit(NoteDetailUiEffect.ShowSnackbar("삭제되었습니다."))
                        _effect.emit(NoteDetailUiEffect.NavigateBack)
                    }
                    is DataResourceResult.Failure -> {
                        _isProcessing.value = false
                        _effect.emit(NoteDetailUiEffect.ShowSnackbar("삭제 실패: ${result.exception.message}"))
                    }
                    else -> _isProcessing.value = false
                }
            }
        }
    }
}