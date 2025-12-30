package com.leafy.features.note.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.ui.utils.LeafyTimeUtils
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.*
import com.subin.leafy.domain.usecase.NoteUseCases
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface NoteUiEffect {
    data class ShowToast(val message: String) : NoteUiEffect
    object NavigateBack : NoteUiEffect
}
@RequiresApi(Build.VERSION_CODES.O)
class NoteViewModel(
    private val noteUseCases: NoteUseCases,
    initialRecords: List<InfusionRecord>?,
    val noteId: String? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteUiState(
        dateTime = LeafyTimeUtils.nowToString()
    ))
    val uiState = _uiState.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _effect = MutableSharedFlow<NoteUiEffect>()
    val effect = _effect.asSharedFlow()

    init {
        if (noteId != null) {
            loadExistingNote(noteId)
        } else if (initialRecords != null) {
            applyTimerRecords(initialRecords)
        }
    }

    private fun loadExistingNote(id: String) {
        viewModelScope.launch {
            _isProcessing.update { true }
            noteUseCases.getNoteById(id).collectLatest { result ->
                when (result) {
                    is DataResourceResult.Success -> {
                        _uiState.update {
                            result.data.toUiState()
                        }
                        _isProcessing.update { false }
                    }
                    is DataResourceResult.Failure -> {
                        _isProcessing.update { false }
                        _effect.emit(NoteUiEffect.ShowToast("데이터를 불러오지 못했습니다."))
                    }
                    else -> _isProcessing.update { false }
                }
            }
        }
    }

    private fun applyTimerRecords(records: List<InfusionRecord>) {
        if (records.isEmpty()) return
        val summarizedTime = records.joinToString(" / ") { it.formattedTime }
        val totalCount = records.size.toString()

        _uiState.update { state ->
            state.copy(
                brewTime = summarizedTime,
                brewCount = totalCount,
                memo = "타이머 세션 기록이 적용되었습니다. (총 ${totalCount}회 우림)"
            )
        }
    }

    fun saveNote() {
        val currentState = _uiState.value

        if (currentState.teaName.isBlank()) {
            viewModelScope.launch { _effect.emit(NoteUiEffect.ShowToast("차 이름을 입력해주세요.")) }
            return
        }

        val hasPhoto = listOf(
            currentState.dryLeafUri, currentState.liquorUri,
            currentState.teawareUri, currentState.additionalUri
        ).any { !it.isNullOrBlank() }

        if (!hasPhoto) {
            viewModelScope.launch { _effect.emit(NoteUiEffect.ShowToast("최소 한 장의 사진을 등록해야 합니다.")) }
            return
        }

        viewModelScope.launch {
            val ownerId = noteUseCases.getCurrentUserId() ?: run {
                _effect.emit(NoteUiEffect.ShowToast("로그인이 필요한 서비스입니다."))
                return@launch
            }

            val finalId = noteId ?: java.util.UUID.randomUUID().toString()
            val domainNote = currentState.toDomain(ownerId, finalId)

            val operation = if (noteId != null) {
                noteUseCases.updateNote(domainNote)
            } else {
                noteUseCases.insertNote(domainNote)
            }

            handleOperation(operation)
        }
    }

    private suspend fun handleOperation(flow: Flow<DataResourceResult<Unit>>) {
        flow.collectLatest { result ->
            when (result) {
                is DataResourceResult.Loading -> _isProcessing.update { true }
                is DataResourceResult.Success -> {
                    _isProcessing.update { false }
                    _effect.emit(NoteUiEffect.ShowToast(if (noteId != null) "수정되었습니다." else "저장되었습니다."))
                    _effect.emit(NoteUiEffect.NavigateBack)
                }
                is DataResourceResult.Failure -> {
                    _isProcessing.update { false }
                    _effect.emit(NoteUiEffect.ShowToast("오류 발생: ${result.exception.message}"))
                }
                else -> _isProcessing.update { false }
            }
        }
    }

    fun updateTeaInfo(name: String? = null, brand: String? = null, type: String? = null, style: String? = null, processing: String? = null, grade: String? = null) {
        _uiState.update { it.copy(teaName = name ?: it.teaName, brandName = brand ?: it.brandName, teaType = type ?: it.teaType, leafStyle = style ?: it.leafStyle, leafProcessing = processing ?: it.leafProcessing, teaGrade = grade ?: it.teaGrade) }
    }

    fun updateContext(dateTime: String? = null, weather: WeatherType? = null, withPeople: String? = null, dryUri: String? = null, liqUri: String? = null, teaUri: String? = null, addUri: String? = null) {
        _uiState.update { it.copy(
            dateTime = dateTime ?: it.dateTime,
            weather = weather ?: it.weather,
            withPeople = withPeople ?: it.withPeople,
            dryLeafUri = dryUri ?: it.dryLeafUri,
            liquorUri = liqUri ?: it.liquorUri,
            teawareUri = teaUri ?: it.teawareUri,
            additionalUri = addUri ?: it.additionalUri
        ) }
    }

    fun updateCondition(temp: String? = null, amount: String? = null, time: String? = null, count: String? = null, teaware: String? = null) {
        _uiState.update { it.copy(waterTemp = temp ?: it.waterTemp, leafAmount = amount ?: it.leafAmount, brewTime = time ?: it.brewTime, brewCount = count ?: it.brewCount, teaware = teaware ?: it.teaware) }
    }

    fun updateSensory(tags: Set<String>? = null, sweet: Float? = null, sour: Float? = null, bitter: Float? = null, salty: Float? = null, umami: Float? = null, body: BodyType? = null, finish: Float? = null, memo: String? = null) {
        _uiState.update { it.copy(selectedTags = tags ?: it.selectedTags, sweetness = sweet ?: it.sweetness, sourness = sour ?: it.sourness, bitterness = bitter ?: it.bitterness, saltiness = salty ?: it.saltiness, umami = umami ?: it.umami, bodyType = body ?: it.bodyType, finishLevel = finish ?: it.finishLevel, memo = memo ?: it.memo) }
    }

    fun updateRating(rating: Int? = null, purchaseAgain: Boolean? = null) {
        _uiState.update { it.copy(rating = rating ?: it.rating, purchaseAgain = purchaseAgain ?: it.purchaseAgain) }
    }
}

