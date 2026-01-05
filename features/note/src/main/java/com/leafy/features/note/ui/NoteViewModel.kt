package com.leafy.features.note.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.ui.utils.LeafyTimeUtils
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.*
import com.subin.leafy.domain.usecase.NoteUseCases
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface NoteUiEffect {
    data class ShowSnackbar(val message: String) : NoteUiEffect
    object NavigateBack : NoteUiEffect
}

class NoteViewModel(
    private val noteUseCases: NoteUseCases,
    private val initialRecords: List<InfusionRecord>?,
    private val noteId: String? = null,
    private val selectedDate: String? = null
) : ViewModel() {

    private val _userInputs = MutableStateFlow<NoteUiState?>(null)
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _initialDataSource: Flow<NoteUiState> = flow {
        val currentUserId = noteUseCases.getCurrentUserId() ?: ""

        if (noteId != null) {
            noteUseCases.getNoteById(currentUserId, noteId)
                .collect { result ->
                    if (result is DataResourceResult.Success) {
                        emit(result.data.toUiState())
                    }
                }
        } else {
            val initialDate = selectedDate ?: LeafyTimeUtils.nowToString()
            var baseState = NoteUiState(dateTime = initialDate)

            initialRecords?.let { records ->
                val summarizedTime = records.joinToString(" / ") { it.formattedTime }
                baseState = baseState.copy(
                    brewTime = summarizedTime,
                    brewCount = records.size.toString(),
                    memo = "타이머 세션 기록이 적용되었습니다. (총 ${records.size}회 우림)"
                )
            }
            emit(baseState)
        }
    }

    val uiState: StateFlow<NoteUiState> = combine(
        _initialDataSource,
        _userInputs,
        _isProcessing
    ) { initial, input, loading ->
        (input ?: initial).copy(isLoading = loading)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NoteUiState(
            isLoading = true,
            dateTime = selectedDate ?: LeafyTimeUtils.nowToString()
        )
    )

    private val _effect = MutableSharedFlow<NoteUiEffect>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effect = _effect.asSharedFlow()

    // --- 기능 함수들 ---

    fun saveNote() {
        viewModelScope.launch {
            val currentState = uiState.value
            val ownerId = noteUseCases.getCurrentUserId() ?: run {
                _effect.emit(NoteUiEffect.ShowSnackbar("로그인이 필요합니다."))
                return@launch
            }

            if (!currentState.canSave) {
                if (currentState.teaName.isBlank()) {
                    _effect.emit(NoteUiEffect.ShowSnackbar("차 이름을 입력해주세요."))
                } else {
                    _effect.emit(NoteUiEffect.ShowSnackbar("최소 한 장의 사진을 등록해야 합니다."))
                }
                return@launch
            }

            val imageMap = mapOf(
                "dryLeaf" to currentState.dryLeafUri,
                "liquor" to currentState.liquorUri,
                "teaware" to currentState.teawareUri,
                "additional" to currentState.additionalUri
            )

            _isProcessing.value = true
            val finalId = noteId ?: UUID.randomUUID().toString()

            val domainNote = currentState.toDomain(ownerId, finalId)

            val operation = if (noteId != null) {
                noteUseCases.updateNote(ownerId, domainNote, imageMap)
            } else {
                noteUseCases.insertNote(domainNote, imageMap)
            }

            operation.collectLatest { result ->
                when (result) {
                    is DataResourceResult.Success -> {
                        _isProcessing.value = false
                        _effect.emit(NoteUiEffect.ShowSnackbar(if (noteId != null) "수정되었습니다." else "저장되었습니다."))
                        _effect.emit(NoteUiEffect.NavigateBack)
                    }
                    is DataResourceResult.Failure -> {
                        _isProcessing.value = false
                        _effect.emit(NoteUiEffect.ShowSnackbar("오류 발생: ${result.exception.message}"))
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun updateState(reducer: (NoteUiState) -> NoteUiState) {
        val current = _userInputs.value ?: uiState.value
        _userInputs.value = reducer(current)
    }

    fun updateTeaInfo(name: String? = null, brand: String? = null, type: String? = null, style: String? = null, processing: String? = null, grade: String? = null) {
        updateState { it.copy(
            teaName = name ?: it.teaName,
            brandName = brand ?: it.brandName,
            teaType = type ?: it.teaType,
            leafStyle = style ?: it.leafStyle,
            leafProcessing = processing ?: it.leafProcessing,
            teaGrade = grade ?: it.teaGrade
        ) }
    }

    fun updateContext(dateTime: String? = null, weather: WeatherType? = null, withPeople: String? = null, dryUri: String? = null, liqUri: String? = null, teaUri: String? = null, addUri: String? = null) {
        updateState { it.copy(
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
        updateState { it.copy(
            waterTemp = temp ?: it.waterTemp,
            leafAmount = amount ?: it.leafAmount,
            brewTime = time ?: it.brewTime,
            brewCount = count ?: it.brewCount,
            teaware = teaware ?: it.teaware
        ) }
    }

    fun updateSensory(tags: Set<String>? = null, sweet: Float? = null, sour: Float? = null, bitter: Float? = null, salty: Float? = null, umami: Float? = null, body: BodyType? = null, finish: Float? = null, memo: String? = null) {
        updateState { it.copy(
            selectedTags = tags ?: it.selectedTags,
            sweetness = sweet ?: it.sweetness,
            sourness = sour ?: it.sourness,
            bitterness = bitter ?: it.bitterness,
            saltiness = salty ?: it.saltiness,
            umami = umami ?: it.umami,
            bodyType = body ?: it.bodyType,
            finishLevel = finish ?: it.finishLevel,
            memo = memo ?: it.memo
        ) }
    }

    fun updateRating(rating: Int? = null, purchaseAgain: Boolean? = null) {
        updateState { it.copy(
            rating = rating ?: it.rating,
            purchaseAgain = purchaseAgain ?: it.purchaseAgain
        ) }
    }
}