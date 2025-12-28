package com.leafy.features.note.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.usecase.NoteUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoteDetailViewModel(
    private val noteUseCases: NoteUseCases,
    private val noteId: String // 🎯 ID 타입을 String으로 변경
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()

    // 로딩 및 에러 상태 관리를 위한 추가 (선택 사항)
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    init {
        loadNoteDetail()
    }

    private fun loadNoteDetail() {
        viewModelScope.launch {
            // 🎯 Flow<DataResourceResult<BrewingNote>>를 collect합니다.
            noteUseCases.getNoteById(noteId).collectLatest { result ->
                when (result) {
                    is DataResourceResult.Loading -> {
                        _isProcessing.update { true }
                    }
                    is DataResourceResult.Success -> {
                        _isProcessing.update { false }
                        // 🎯 도메인 모델(BrewingNote)을 UI 상태로 변환하여 적용
                        _uiState.update { result.data.toUiState() }
                    }
                    is DataResourceResult.Failure -> {
                        _isProcessing.update { false }
                        // 에러 발생 시 처리 (예: Toast용 Effect 전송 등)
                    }
                    else -> _isProcessing.update { false }
                }
            }
        }
    }

    // 🎯 삭제 기능 추가 (NoteDetailScreen에서 사용)
    fun deleteNote(onSuccess: () -> Unit) {
        viewModelScope.launch {
            noteUseCases.deleteNote(noteId).collectLatest { result ->
                if (result is DataResourceResult.Success) {
                    onSuccess()
                }
            }
        }
    }
}

/**
 * 🎯 BrewingNote(Domain) -> NoteUiState(UI) 변환 확장 함수
 * NoteViewModel의 toDomain과 반대 역할을 합니다.
 */
fun BrewingNote.toUiState(): NoteUiState {
    return NoteUiState(
        teaName = teaInfo.name,
        brandName = teaInfo.brand,
        teaType = teaInfo.type,
        leafStyle = teaInfo.leafStyle,
        leafProcessing = teaInfo.processing,
        teaGrade = teaInfo.grade,

        waterTemp = condition.waterTemp,
        leafAmount = condition.leafAmount,
        brewTime = condition.brewTime,
        brewCount = condition.brewCount,
        teaware = condition.teaware,

        dateTime = context.dateTime,
        weather = context.weather,
        withPeople = context.withPeople,
        dryLeafUri = context.dryLeafUri,
        liquorUri = context.liquorUri,
        teawareUri = context.teawareUri,
        additionalUri = context.additionalUri,

        selectedTags = evaluation.selectedTags,
        sweetness = evaluation.sweetness,
        sourness = evaluation.sourness,
        bitterness = evaluation.bitterness,
        saltiness = evaluation.saltiness,
        umami = evaluation.umami,
        bodyType = evaluation.bodyType,
        finishLevel = evaluation.finishLevel,
        memo = evaluation.memo,

        rating = ratingInfo.stars,
        purchaseAgain = ratingInfo.purchaseAgain
    )
}