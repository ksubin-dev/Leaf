package com.leafy.features.note.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.subin.leafy.domain.model.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// 1. 일회성 이벤트 정의
sealed interface NoteUiEffect {
    data class ShowToast(val message: String) : NoteUiEffect
    object NavigateBack : NoteUiEffect
}

class NoteViewModel : ViewModel() {

    // 2. 전체 화면 상태
    private val _uiState = MutableStateFlow(NoteUiState())
    val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()

    // 3. 처리 중 상태 (저장 버튼 비활성화 및 로딩 표시용)
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    // 4. 이벤트 채널
    private val _effect = Channel<NoteUiEffect>()
    val effect = _effect.receiveAsFlow()

    /**
     * 초기 데이터 주입 (Timer 모듈 연결용)
     */
    fun initData(brewTime: String?, brewCount: String?) {
        _uiState.update { it.copy(
            brewTime = brewTime ?: it.brewTime,
            brewCount = brewCount ?: it.brewCount
        ) }
    }


    // 1. Basic Tea Information 섹션 업데이트
    fun updateTeaInfo(
        name: String? = null,
        brand: String? = null,
        type: String? = null,
        style: String? = null,
        processing: String? = null,
        grade: String? = null
    ) {
        _uiState.update { it.copy(
            teaName = name ?: it.teaName,
            brandName = brand ?: it.brandName,
            teaType = type ?: it.teaType,
            leafStyle = style ?: it.leafStyle,
            leafProcessing = processing ?: it.leafProcessing,
            teaGrade = grade ?: it.teaGrade
        ) }
    }

    // 2. Tasting Context 섹션 업데이트
    fun updateContext(
        dateTime: String? = null,
        weather: WeatherType? = null,
        withPeople: String? = null
    ) {
        _uiState.update { it.copy(
            dateTime = dateTime ?: it.dateTime,
            weather = weather ?: it.weather,
            withPeople = withPeople ?: it.withPeople
        ) }
    }

    // 3. Brewing Condition 섹션 업데이트
    fun updateCondition(
        waterTemp: String? = null,
        leafAmount: String? = null,
        brewTime: String? = null,
        brewCount: String? = null,
        teaware: String? = null
    ) {
        _uiState.update { it.copy(
            waterTemp = waterTemp ?: it.waterTemp,
            leafAmount = leafAmount ?: it.leafAmount,
            brewTime = brewTime ?: it.brewTime,
            brewCount = brewCount ?: it.brewCount,
            teaware = teaware ?: it.teaware
        ) }
    }

    // 4. Sensory Evaluation 섹션 업데이트
    fun updateSensory(
        tags: Set<String>? = null,
        sweetness: Int? = null,
        sourness: Int? = null,
        bitterness: Int? = null,
        saltiness: Int? = null,
        umami: Int? = null,
        bodyIndex: Int? = null,
        finishLevel: Float? = null,
        memo: String? = null
    ) {
        _uiState.update { it.copy(
            selectedTags = tags ?: it.selectedTags,
            sweetness = sweetness ?: it.sweetness,
            sourness = sourness ?: it.sourness,
            bitterness = bitterness ?: it.bitterness,
            saltiness = saltiness ?: it.saltiness,
            umami = umami ?: it.umami,
            bodyIndex = bodyIndex ?: it.bodyIndex,
            finishLevel = finishLevel ?: it.finishLevel,
            memo = memo ?: it.memo
        ) }
    }

    // 5. Final Rating 섹션 업데이트
    fun updateRating(
        rating: Int? = null,
        purchaseAgain: Boolean? = null
    ) {
        _uiState.update { it.copy(
            rating = rating ?: it.rating,
            purchaseAgain = purchaseAgain ?: it.purchaseAgain
        ) }
    }

    // ---------------- [비즈니스 로직] ----------------

    fun saveNote() {
        val currentState = _uiState.value

        // 유효성 검사
        if (currentState.teaName.isBlank()) {
            viewModelScope.launch { _effect.send(NoteUiEffect.ShowToast("차 이름을 입력해주세요!")) }
            return
        }

        viewModelScope.launch {
            _isProcessing.update { true } // 로딩 시작

            try {
                // UI State를 도메인 모델로 변환 (Mapper)
                val note = currentState.toDomain()

                // TODO: Repository 또는 UseCase 호출
                // noteUseCases.saveNote(note)

                _effect.send(NoteUiEffect.ShowToast("기록이 성공적으로 저장되었습니다."))
                _effect.send(NoteUiEffect.NavigateBack)
            } catch (e: Exception) {
                _effect.send(NoteUiEffect.ShowToast("저장 실패: ${e.message}"))
            } finally {
                _isProcessing.update { false } // 로딩 종료
            }
        }
    }

    private fun NoteUiState.toDomain() = BrewingNote(
        teaInfo = TeaInfo(teaName, brandName, teaType, leafStyle, leafProcessing, teaGrade),
        condition = BrewingCondition(waterTemp, leafAmount, brewTime, brewCount, teaware),
        evaluation = SensoryEvaluation(selectedTags, sweetness, sourness, bitterness, saltiness, umami, bodyIndex, finishLevel, memo),
        ratingInfo = RatingInfo(rating, purchaseAgain),
        context = NoteContext(weather, withPeople)
    )
}