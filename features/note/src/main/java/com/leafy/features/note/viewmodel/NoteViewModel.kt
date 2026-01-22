package com.leafy.features.note.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leafy.shared.utils.LeafyTimeUtils
import com.leafy.shared.utils.ImageCompressor
import com.subin.leafy.domain.common.DataResourceResult
import com.subin.leafy.domain.model.BodyType
import com.subin.leafy.domain.model.BrewingNote
import com.subin.leafy.domain.model.BrewingRecipe
import com.subin.leafy.domain.model.FlavorTag
import com.subin.leafy.domain.model.NoteMetadata
import com.subin.leafy.domain.model.PostSocialState
import com.subin.leafy.domain.model.PostStatistics
import com.subin.leafy.domain.model.RatingInfo
import com.subin.leafy.domain.model.SensoryEvaluation
import com.subin.leafy.domain.model.TeaInfo
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.model.WeatherType
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
import androidx.core.net.toUri
import com.leafy.shared.ui.model.BrewingSessionNavArgs
import com.subin.leafy.domain.model.TeawareType
import kotlinx.serialization.json.Json

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

    fun updateTeaName(name: String) = _uiState.update { it.copy(teaName = name) }
    fun updateTeaBrand(brand: String) = _uiState.update { it.copy(teaBrand = brand) }
    fun updateTeaType(type: TeaType) = _uiState.update { it.copy(teaType = type) }
    fun updateTeaOrigin(origin: String) = _uiState.update { it.copy(teaOrigin = origin) }
    fun updateTeaLeafStyle(style: String) = _uiState.update { it.copy(teaLeafStyle = style) }
    fun updateTeaGrade(grade: String) = _uiState.update { it.copy(teaGrade = grade) }

    fun updateWaterTemp(temp: String) = _uiState.update { it.copy(waterTemp = temp) }
    fun updateLeafAmount(amount: String) = _uiState.update { it.copy(leafAmount = amount) }
    fun updateWaterAmount(amount: String) = _uiState.update { it.copy(waterAmount = amount) }
    fun updateBrewTime(time: String) = _uiState.update { it.copy(brewTime = time) }
    fun updateInfusionCount(count: String) = _uiState.update { it.copy(infusionCount = count) }
    fun updateTeaware(teaware: TeawareType) = _uiState.update { it.copy(teaware = teaware) }

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

    fun updateDateTime(date: String) = _uiState.update { it.copy(selectedDateString = date) }
    fun updateWeather(weather: WeatherType) = _uiState.update { it.copy(selectedWeather = weather) }
    fun updateWithPeople(people: String) = _uiState.update { it.copy(withPeople = people) }

    fun updateStarRating(stars: Int) = _uiState.update { it.copy(starRating = stars) }
    fun updatePurchaseAgain(willBuy: Boolean) = _uiState.update { it.copy(purchaseAgain = willBuy) }

    fun updateIsPublic(isPublic: Boolean) = _uiState.update { it.copy(isPublic = isPublic) }

    fun addImages(uris: List<Uri>) {
        _uiState.update {
            val newConstant = (it.selectedImages + uris).take(5)
            it.copy(selectedImages = newConstant)
        }
    }


    fun removeImage(uri: Uri) {
        _uiState.update { state ->
            state.copy(selectedImages = state.selectedImages - uri)
        }
    }

    fun userMessageShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun saveNote() {
        val state = uiState.value
        if (!state.isFormValid) {
            _uiState.update { it.copy(errorMessage = "차 이름과 사진 등 필수 정보를 입력해주세요.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val userIdResult = userUseCases.getCurrentUserId()
                if (userIdResult is DataResourceResult.Failure) {
                    throw Exception("로그인 정보를 찾을 수 없습니다.")
                }
                val userId = (userIdResult as DataResourceResult.Success).data

                val targetId = state.noteId ?: UUID.randomUUID().toString()

                val finalImageUrls = state.selectedImages.map { uri ->
                    async {
                        val uriString = uri.toString()
                        if (uriString.startsWith("http")) {
                            uriString
                        } else {
                            val compressedPath = imageCompressor.compressImage(uriString)
                            val uploadResult = imageUseCases.uploadImage(
                                uri = compressedPath,
                                folder = "notes/$userId/$targetId"
                            )
                            if (uploadResult is DataResourceResult.Success) {
                                uploadResult.data
                            } else {
                                throw Exception("이미지 업로드 실패")
                            }
                        }
                    }
                }.awaitAll()

                val isEditMode = state.noteId != null
                val currentSystemTime = System.currentTimeMillis()

                val selectedBrewDate = LeafyTimeUtils.dateStringToTimestamp(state.selectedDateString)

                val finalCreatedAt = if (isEditMode) {
                    state.originalCreatedAt ?: currentSystemTime
                } else {
                    currentSystemTime
                }

                val newNote = BrewingNote(
                    id = targetId,
                    ownerId = userId,
                    isPublic = state.isPublic,

                    date = selectedBrewDate,
                    createdAt = finalCreatedAt,

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
                        imageUrls = finalImageUrls
                    ),
                    stats = PostStatistics(0, 0, 0, 0),
                    myState = PostSocialState(false, false),
                )

                val result = if (isEditMode) {
                    noteUseCases.updateNote(newNote)
                } else {
                    noteUseCases.saveNote(newNote)
                }

                if (result is DataResourceResult.Success) {
                    _uiState.update { it.copy(isLoading = false, isSaveSuccess = true) }
                } else {
                    throw (result as DataResourceResult.Failure).exception
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "저장 실패")
                }
            }
        }
    }

    fun loadNoteForEdit(noteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val myIdResult = userUseCases.getCurrentUserId()
            val myId = if (myIdResult is DataResourceResult.Success) myIdResult.data else null

            val result = noteUseCases.getNoteDetail(noteId = noteId)

            if (result is DataResourceResult.Success) {
                val note = result.data
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        noteId = note.id,

                        originalCreatedAt = note.createdAt,

                        selectedDateString = LeafyTimeUtils.millisToDateString(note.date),

                        isPublic = note.isPublic,

                        teaName = note.teaInfo.name,
                        teaBrand = note.teaInfo.brand,
                        teaType = note.teaInfo.type,
                        teaOrigin = note.teaInfo.origin,
                        teaLeafStyle = note.teaInfo.leafStyle,
                        teaGrade = note.teaInfo.grade,

                        waterTemp = note.recipe.waterTemp.toString(),
                        leafAmount = note.recipe.leafAmount.toString(),
                        waterAmount = note.recipe.waterAmount.toString(),
                        brewTime = note.recipe.brewTimeSeconds.toString(),
                        infusionCount = note.recipe.infusionCount.toString(),
                        teaware = note.recipe.teaware,

                        flavorTags = note.evaluation.flavorTags,
                        sweetness = note.evaluation.sweetness.toFloat(),
                        sourness = note.evaluation.sourness.toFloat(),
                        bitterness = note.evaluation.bitterness.toFloat(),
                        astringency = note.evaluation.astringency.toFloat(),
                        umami = note.evaluation.umami.toFloat(),
                        body = note.evaluation.body,
                        finish = note.evaluation.finishLevel.toFloat(),
                        memo = note.evaluation.memo,

                        selectedWeather = note.metadata.weather,
                        withPeople = note.metadata.mood,

                        starRating = note.rating.stars,
                        purchaseAgain = note.rating.purchaseAgain,

                        selectedImages = note.metadata.imageUrls.map { it.toUri() }
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "노트 정보를 불러오지 못했습니다.")
                }
            }
        }
    }

    fun initFromTimerData(jsonArgs: String) {
        viewModelScope.launch {
            try {
                val args = Json.decodeFromString<BrewingSessionNavArgs>(jsonArgs)

                val teaTypeEnum = try {
                    TeaType.valueOf(args.teaType)
                } catch (e: Exception) {
                    TeaType.UNKNOWN
                }

                val teawareEnum = try {
                    TeawareType.valueOf(args.teaware)
                } catch (e: Exception) {
                    TeawareType.MUG
                }

                val totalTimeSeconds = args.records.sumOf { it.timeSeconds }

                val recordMemo = buildString {
                    append("[타이머 기록]\n")
                    args.records.forEach { record ->
                        append("${record.count}회차: ${record.waterTemp}°C / ${record.timeSeconds}초\n")
                    }
                }

                _uiState.update { state ->
                    state.copy(
                        teaName = if (state.teaName.isBlank()) args.teaName else state.teaName,
                        teaType = if (state.teaType == TeaType.UNKNOWN) teaTypeEnum else state.teaType,

                        waterTemp = args.waterTemp.toString(),
                        leafAmount = args.leafAmount.toString(),
                        waterAmount = args.waterAmount.toString(),
                        infusionCount = args.records.size.toString(),
                        brewTime = totalTimeSeconds.toString(),
                        teaware = teawareEnum,
                        memo = if (state.memo.isBlank()) recordMemo else "${state.memo}\n\n$recordMemo",
                        userMessage = "타이머 기록이 적용되었습니다."
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(errorMessage = "데이터를 불러오는데 실패했습니다.") }
            }
        }
    }

}