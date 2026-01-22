package com.subin.leafy.data.model.dto

data class BrewingNoteDTO(
    val _id: String = "",
    val userId: String = "",
    // TeaInfo
    val teaName: String = "",
    val teaBrand: String = "",
    val teaType: String = "",
    val leafStyle: String = "",
    val processing: String = "",
    val teaGrade: String = "",
    // BrewingCondition
    val waterTemp: String = "",
    val leafAmount: String = "",
    val brewTime: String = "",
    val brewCount: String = "",
    val teaware: String = "",
    // SensoryEvaluation
    val selectedTags: List<String> = emptyList(),
    val sweetness: Int = 0,
    val sourness: Int = 0,
    val bitterness: Int = 0,
    val saltiness: Int = 0,
    val umami: Int = 0,
    val bodyType: String = "MEDIUM",
    val finishLevel: Float = 0.5f,
    val memo: String = "",
    // RatingInfo
    val stars: Int = 0,
    val purchaseAgain: Boolean? = null,
    // NoteContext
    val weather: String = "INDOOR",
    val withPeople: String = "",
    val dryLeafUri: String? = null,
    val liquorUri: String? = null,
    val teawareUri: String? = null,
    val additionalUri: String? = null,
    // Timestamp
    val createdAt: Long = 0L
)