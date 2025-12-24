package com.leafy.features.note.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.*
import com.subin.leafy.domain.usecase.NoteUseCases
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// UI에서 처리할 일회성 이벤트
sealed interface NoteUiEffect {
    data class ShowToast(val message: String) : NoteUiEffect
    object NavigateBack : NoteUiEffect
}

class NoteViewModel(
    private val noteUseCases: NoteUseCases,
    initialRecords: List<InfusionRecord>?
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState = _uiState.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _effect = Channel<NoteUiEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        initialRecords?.let { records ->
            applyTimerRecords(records)
        }
    }

    private fun applyTimerRecords(records: List<InfusionRecord>) {
        if (records.isEmpty()) return

        val lastBrew = records.first()

        _uiState.update { state ->
            state.copy(
                brewTime = lastBrew.formattedTime,
                brewCount = records.size.toString(),
                memo = "타이머 세션 기록이 성공적으로 불러와졌습니다. (${records.size}회 우림)"
            )
        }
    }

    /**
     * CUD 작업을 일괄 처리하는 공통 함수
     */
    private suspend fun handleOperation(flow: Flow<DataResourceResult<Unit>>) {
        flow.collectLatest { result ->
            when (result) {
                is DataResourceResult.Loading -> {
                    _isProcessing.update { true }
                }
                is DataResourceResult.Success -> {
                    _isProcessing.update { false }
                    _effect.send(NoteUiEffect.ShowToast("성공적으로 저장되었습니다."))
                    _effect.send(NoteUiEffect.NavigateBack)
                }
                is DataResourceResult.Failure -> {
                    _isProcessing.update { false }
                    _effect.send(NoteUiEffect.ShowToast("실패: ${result.exception.message}"))
                }
                else -> Unit
            }
        }
    }

    // --- 비즈니스 로직 (UseCase 호출) ---

    fun saveNote() {
        if (_uiState.value.teaName.isBlank()) {
            viewModelScope.launch {
                _effect.send(NoteUiEffect.ShowToast("차 이름을 입력해주세요."))
            }
            return
        }

        viewModelScope.launch {
            val ownerId = noteUseCases.getCurrentUserId()
            if (ownerId == null) {
                _effect.send(NoteUiEffect.ShowToast("로그인이 필요한 서비스입니다."))
                return@launch
            }
            val domainNote = _uiState.value.toDomain(ownerId)
            handleOperation(noteUseCases.insertNote(domainNote))
        }
    }

    // --- UI 상태 업데이트 함수들 ---

    fun updateTeaInfo(
        name: String? = null,
        brand: String? = null,
        type: String? = null,
        style: String? = null,
        processing: String? = null,
        grade: String? = null
    ) {
        _uiState.update {
            it.copy(
                teaName = name ?: it.teaName,
                brandName = brand ?: it.brandName,
                teaType = type ?: it.teaType,
                leafStyle = style ?: it.leafStyle,
                leafProcessing = processing ?: it.leafProcessing,
                teaGrade = grade ?: it.teaGrade
            )
        }
    }

    fun updateContext(
        dateTime: String? = null,
        weather: WeatherType? = null,
        withPeople: String? = null
    ) {
        _uiState.update {
            it.copy(
                dateTime = dateTime ?: it.dateTime,
                weather = weather ?: it.weather,
                withPeople = withPeople ?: it.withPeople
            )
        }
    }

    fun updateCondition(
        temp: String? = null,
        amount: String? = null,
        time: String? = null,
        count: String? = null,
        teaware: String? = null
    ) {
        _uiState.update {
            it.copy(
                waterTemp = temp ?: it.waterTemp,
                leafAmount = amount ?: it.leafAmount,
                brewTime = time ?: it.brewTime,
                brewCount = count ?: it.brewCount,
                teaware = teaware ?: it.teaware
            )
        }
    }

    fun updateSensory(
        tags: Set<String>? = null,
        sweet: Int? = null,
        sour: Int? = null,
        bitter: Int? = null,
        salty: Int? = null,
        umami: Int? = null,
        body: BodyType? = null,
        finish: Float? = null,
        memo: String? = null
    ) {
        _uiState.update {
            it.copy(
                selectedTags = tags ?: it.selectedTags,
                sweetness = sweet ?: it.sweetness,
                sourness = sour ?: it.sourness,
                bitterness = bitter ?: it.bitterness,
                saltiness = salty ?: it.saltiness,
                umami = umami ?: it.umami,
                bodyType = body ?: it.bodyType,
                finishLevel = finish ?: it.finishLevel,
                memo = memo ?: it.memo
            )
        }
    }

    fun updateRating(rating: Int? = null, purchaseAgain: Boolean? = null) {
        _uiState.update {
            it.copy(
                rating = rating ?: it.rating,
                purchaseAgain = purchaseAgain ?: it.purchaseAgain
            )
        }
    }
}

/**
 * NoteUiState -> BrewingNote 변환
 */
fun NoteUiState.toDomain(ownerId: String): BrewingNote {
    return BrewingNote(
        id = java.util.UUID.randomUUID().toString(),//인데 파이어베이스라면 이거 바꿔줘야함
        ownerId = ownerId,
        teaInfo = TeaInfo(
            name = teaName,
            brand = brandName,
            type = teaType,
            leafStyle = leafStyle,
            processing = leafProcessing,
            grade = teaGrade
        ),
        condition = BrewingCondition(
            waterTemp = waterTemp,
            leafAmount = leafAmount,
            brewTime = brewTime,
            brewCount = brewCount,
            teaware = teaware
        ),
        evaluation = SensoryEvaluation(
            selectedTags = selectedTags,
            sweetness = sweetness,
            sourness = sourness,
            bitterness = bitterness,
            saltiness = saltiness,
            umami = umami,
            bodyType = bodyType,
            finishLevel = finishLevel,
            memo = memo
        ),
        ratingInfo = RatingInfo(
            stars = rating,
            purchaseAgain = purchaseAgain
        ),
        context = NoteContext(
            weather = weather,
            withPeople = withPeople
        )
    )
}