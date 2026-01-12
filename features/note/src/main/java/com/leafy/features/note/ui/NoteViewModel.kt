package com.leafy.features.note.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.ui.utils.LeafyTimeUtils // ★ 유틸 임포트
import com.leafy.shared.util.ImageCompressor
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.*
import com.subin.leafy.domain.usecase.ImageUseCases
import com.subin.leafy.domain.usecase.NoteUseCases
import com.subin.leafy.domain.usecase.UserUseCases
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.roundToInt

class NoteViewModel(
    private val noteUseCases: NoteUseCases,
    private val userUseCases: UserUseCases,
    private val imageUseCases: ImageUseCases,
    private val imageCompressor: ImageCompressor
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        NoteUiState(
            selectedDateString = LeafyTimeUtils.nowToString()
        )
    )
    val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()

    // --- 1. 기본 정보 ---
    fun updateTeaName(name: String) = _uiState.update { it.copy(teaName = name) }
    fun updateTeaBrand(brand: String) = _uiState.update { it.copy(teaBrand = brand) }
    fun updateTeaType(type: TeaType) = _uiState.update { it.copy(teaType = type) }
    fun updateTeaOrigin(origin: String) = _uiState.update { it.copy(teaOrigin = origin) }
    fun updateTeaLeafStyle(style: String) = _uiState.update { it.copy(teaLeafStyle = style) }
    fun updateTeaGrade(grade: String) = _uiState.update { it.copy(teaGrade = grade) }

    // --- 2. 레시피 ---
    fun updateWaterTemp(temp: String) = _uiState.update { it.copy(waterTemp = temp) }
    fun updateLeafAmount(amount: String) = _uiState.update { it.copy(leafAmount = amount) }
    fun updateWaterAmount(amount: String) = _uiState.update { it.copy(waterAmount = amount) }
    fun updateBrewTime(time: String) = _uiState.update { it.copy(brewTime = time) }
    fun updateInfusionCount(count: String) = _uiState.update { it.copy(infusionCount = count) }
    fun updateTeaware(teaware: String) = _uiState.update { it.copy(teaware = teaware) }

    // --- 3. 감각 평가 ---
    fun updateFlavorTag(tag: FlavorTag) {
        _uiState.update { state ->
            val currentTags = state.flavorTags.toMutableList()
            if (currentTags.contains(tag)) {
                currentTags.remove(tag)
            } else {
                currentTags.add(tag)
            }
            state.copy(flavorTags = currentTags)
        }
    }

    fun updateSweetness(v: Float) = _uiState.update { it.copy(sweetness = v) }
    fun updateSourness(v: Float) = _uiState.update { it.copy(sourness = v) }
    fun updateBitterness(v: Float) = _uiState.update { it.copy(bitterness = v) }
    fun updateAstringency(v: Float) = _uiState.update { it.copy(astringency = v) }
    fun updateUmami(v: Float) = _uiState.update { it.copy(umami = v) }
    fun updateBodyType(type: BodyType) = _uiState.update { it.copy(body = type) }
    fun updateFinish(v: Float) = _uiState.update { it.copy(finish = v) }
    fun updateMemo(text: String) = _uiState.update { it.copy(memo = text) }

    // --- 4. 테이스팅 환경 (추가됨) ---
    // UI에서는 String으로 날짜를 다룸 (YYYY-MM-DD)
    fun updateDateTime(date: String) = _uiState.update { it.copy(selectedDateString = date) }
    fun updateWeather(weather: WeatherType) = _uiState.update { it.copy(selectedWeather = weather) }
    fun updateWithPeople(people: String) = _uiState.update { it.copy(withPeople = people) }

    // --- 5. 최종 평가 ---
    fun updateStarRating(stars: Int) = _uiState.update { it.copy(starRating = stars) }
    fun updatePurchaseAgain(willBuy: Boolean) = _uiState.update { it.copy(purchaseAgain = willBuy) }

    // --- 6. 이미지 ---
    fun addImages(uris: List<Uri>) {
        _uiState.update {
            val newConstant = (it.selectedImages + uris).take(5)
            it.copy(selectedImages = newConstant)
        }
    }

    // --- 날짜 업데이트 (DatePicker에서 호출) ---
    fun updateDateFromMillis(millis: Long) {
        val dateString = LeafyTimeUtils.millisToDateString(millis)
        _uiState.update { it.copy(selectedDateString = dateString) }
    }

    fun removeImage(uri: Uri) {
        _uiState.update { state ->
            state.copy(selectedImages = state.selectedImages - uri)
        }
    }

    fun userMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // --- 7. 저장 ---
    fun saveNote() {
        val state = uiState.value
        if (!state.isFormValid) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                // 1. 유저 ID 확인
                val userIdResult = userUseCases.getCurrentUserId()
                if (userIdResult is DataResourceResult.Failure) {
                    throw Exception("로그인 정보를 찾을 수 없습니다.")
                }
                val userId = (userIdResult as DataResourceResult.Success).data

                // 2. 이미지 업로드
                val uploadedImageUrls = state.selectedImages.map { uri ->
                    async {
                        val compressedPath = imageCompressor.compressImage(uri.toString())
                        val uploadResult = imageUseCases.uploadImage(
                            uri = compressedPath,
                            folder = "notes/$userId/${System.currentTimeMillis()}"
                        )
                        if (uploadResult is DataResourceResult.Success) {
                            uploadResult.data
                        } else {
                            throw Exception("이미지 업로드 실패")
                        }
                    }
                }.awaitAll()

                // 3. 날짜 변환
                val createdTime = LeafyTimeUtils.dateStringToTimestamp(state.selectedDateString)

                // 4. 객체 생성
                val newNote = BrewingNote(
                    id = UUID.randomUUID().toString(),
                    ownerId = userId,
                    isPublic = false,
                    teaInfo = TeaInfo(
                        name = state.teaName,
                        brand = state.teaBrand,
                        type = state.teaType,
                        origin = state.teaOrigin,
                        leafStyle = state.teaLeafStyle,
                        grade = state.teaGrade
                    ),
                    recipe = BrewingRecipe(
                        waterTemp = state.waterTemp.toIntOrNull() ?: 90,
                        leafAmount = state.leafAmount.toFloatOrNull() ?: 3f,
                        waterAmount = state.waterAmount.toIntOrNull() ?: 150,
                        brewTimeSeconds = state.brewTime.toIntOrNull() ?: 180,
                        infusionCount = state.infusionCount.toIntOrNull() ?: 1,
                        teaware = state.teaware
                    ),
                    evaluation = SensoryEvaluation(
                        flavorTags = state.flavorTags,
                        sweetness = state.sweetness.roundToInt(),
                        sourness = state.sourness.roundToInt(),
                        bitterness = state.bitterness.roundToInt(),
                        astringency = state.astringency.roundToInt(),
                        umami = state.umami.roundToInt(),
                        body = state.body,
                        finishLevel = state.finish.roundToInt(),
                        memo = state.memo
                    ),
                    rating = RatingInfo(
                        stars = state.starRating,
                        purchaseAgain = state.purchaseAgain
                    ),
                    metadata = NoteMetadata(
                        weather = state.selectedWeather,
                        mood = state.withPeople,
                        imageUrls = uploadedImageUrls
                    ),
                    stats = PostStatistics(0, 0, 0, 0),
                    myState = PostSocialState(false, false),
                    createdAt = createdTime
                )

                // 5. DB 저장
                val saveResult = noteUseCases.saveNote(newNote)
                if (saveResult is DataResourceResult.Success) {
                    _uiState.update { it.copy(isLoading = false, isSaveSuccess = true) }
                } else {
                    throw (saveResult as DataResourceResult.Failure).exception
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "저장 실패")
                }
            }
        }
    }

    // --- 8. 수정 모드: 기존 노트 불러오기 ---
    fun loadNoteForEdit(noteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. 노트 상세 정보 가져오기
            val result = noteUseCases.getNoteDetail(noteId)

            if (result is DataResourceResult.Success) {
                val note = result.data

                // 2. 도메인 모델(Note) -> UI 상태(UiState)로 매핑
                // Int, Float -> String 변환 과정이 필요합니다.
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,

                        // [기본 정보]
                        teaName = note.teaInfo.name,
                        teaBrand = note.teaInfo.brand,
                        teaType = note.teaInfo.type,
                        teaOrigin = note.teaInfo.origin,
                        teaLeafStyle = note.teaInfo.leafStyle,
                        teaGrade = note.teaInfo.grade,

                        // [레시피] (숫자 -> 문자열)
                        waterTemp = note.recipe.waterTemp.toString(),
                        leafAmount = note.recipe.leafAmount.toString(), // 3.0 -> "3.0"
                        waterAmount = note.recipe.waterAmount.toString(),
                        brewTime = note.recipe.brewTimeSeconds.toString(),
                        infusionCount = note.recipe.infusionCount.toString(),
                        teaware = note.recipe.teaware,

                        // [감각 평가]
                        flavorTags = note.evaluation.flavorTags,
                        sweetness = note.evaluation.sweetness.toFloat(),
                        sourness = note.evaluation.sourness.toFloat(),
                        bitterness = note.evaluation.bitterness.toFloat(),
                        astringency = note.evaluation.astringency.toFloat(),
                        umami = note.evaluation.umami.toFloat(),
                        body = note.evaluation.body,
                        finish = note.evaluation.finishLevel.toFloat(),
                        memo = note.evaluation.memo,

                        // [환경]
                        selectedDateString = LeafyTimeUtils.millisToDateString(note.createdAt),
                        selectedWeather = note.metadata.weather,
                        withPeople = note.metadata.mood, // mood 필드를 withPeople로 사용 중

                        // [최종 평가]
                        starRating = note.rating.stars,
                        purchaseAgain = note.rating.purchaseAgain,

                        // [이미지] (String Url -> Uri 파싱)
                        // 주의: 로컬 Uri가 아니라 네트워크 Url이라서 PhotoSection이 잘 처리해야 함
                        selectedImages = note.metadata.imageUrls.map { android.net.Uri.parse(it) }
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "노트 정보를 불러오지 못했습니다.")
                }
            }
        }
    }

}