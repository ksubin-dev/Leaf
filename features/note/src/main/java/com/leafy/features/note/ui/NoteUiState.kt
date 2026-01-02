package com.leafy.features.note.ui

import com.subin.leafy.domain.model.*

data class NoteUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false,

    // 1. Tasting Context
    val dateTime: String = "",
    val weather: WeatherType = WeatherType.INDOOR,
    val withPeople: String = "",
    val dryLeafUri: String? = null,
    val liquorUri: String? = null,
    val teawareUri: String? = null,
    val additionalUri: String? = null,

    // 2. Tea Information
    val teaName: String = "",
    val brandName: String = "",
    val teaType: String = "Black",
    val leafStyle: String = "Loose Leaf",
    val leafProcessing: String = "Whole Leaf",
    val teaGrade: String = "OP",

    // 3. Brewing Condition
    val waterTemp: String = "",
    val leafAmount: String = "",
    val brewTime: String = "",
    val brewCount: String = "1",
    val teaware: String = "찻주전자",

    // 4. Sensory Evaluation
    val selectedTags: Set<String> = emptySet(),
    val sweetness: Float = 0f,
    val sourness: Float = 0f,
    val bitterness: Float = 0f,
    val saltiness: Float = 0f,
    val umami: Float = 0f,
    val bodyType: BodyType = BodyType.MEDIUM,
    val finishLevel: Float = 0.5f,
    val memo: String = "",

    // 5. Final Rating
    val rating: Int = 0,
    val purchaseAgain: Boolean? = null
) {
    val isError: Boolean get() = errorMessage != null

    private val hasAtLeastOnePhoto: Boolean
        get() = !dryLeafUri.isNullOrBlank() ||
                !liquorUri.isNullOrBlank() ||
                !teawareUri.isNullOrBlank() ||
                !additionalUri.isNullOrBlank()

    val canSave: Boolean
        get() = teaName.isNotBlank() && !isLoading && hasAtLeastOnePhoto

    fun toDomain(ownerId: String, noteId: String): BrewingNote = BrewingNote(
        id = noteId,
        ownerId = ownerId,
        teaInfo = TeaInfo(
            name = teaName, brand = brandName, type = teaType,
            leafStyle = leafStyle, processing = leafProcessing, grade = teaGrade
        ),
        condition = BrewingCondition(
            waterTemp = waterTemp, leafAmount = leafAmount,
            brewTime = brewTime, brewCount = brewCount, teaware = teaware
        ),
        evaluation = SensoryEvaluation(
            selectedTags = selectedTags, sweetness = sweetness,
            sourness = sourness, bitterness = bitterness,
            saltiness = saltiness, umami = umami,
            bodyType = bodyType, finishLevel = finishLevel, memo = memo
        ),
        ratingInfo = RatingInfo(stars = rating, purchaseAgain = purchaseAgain),
        context = NoteContext(
            dateTime = dateTime, weather = weather, withPeople = withPeople,
            dryLeafUri = dryLeafUri, liquorUri = liquorUri,
            teawareUri = teawareUri, additionalUri = additionalUri
        )
    )
}