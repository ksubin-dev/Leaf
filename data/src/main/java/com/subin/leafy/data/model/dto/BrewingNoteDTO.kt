package com.subin.leafy.data.model.dto

data class BrewingNoteDTO(
    val _id: String = "",
    val userId: String = "",

    // 1. TeaInfo
    val teaName: String = "",
    val teaBrand: String = "",
    val teaType: String = "",
    val leafStyle: String = "",
    val processing: String = "",
    val teaGrade: String = "",

    // 2. BrewingCondition
    val waterTemp: String = "",
    val leafAmount: String = "",
    val brewTime: String = "",
    val brewCount: String = "",
    val teaware: String = "",

    // 3. SensoryEvaluation
    val selectedTags: List<String> = emptyList(),
    val sweetness: Float = 0f,
    val sourness: Float = 0f,
    val bitterness: Float = 0f,
    val saltiness: Float = 0f,
    val umami: Float = 0f,
    val bodyType: String = "MEDIUM",
    val finishLevel: Float = 0.5f,
    val memo: String = "",

    // 4. RatingInfo
    val stars: Int = 0,
    val purchaseAgain: Boolean? = null,

    // 5. NoteContext
    val dateTime: String = "",
    val weather: String = "INDOOR",
    val withPeople: String = "",
    val dryLeafUri: String? = null,
    val liquorUri: String? = null,
    val teawareUri: String? = null,
    val additionalUri: String? = null,

    val likeCount: Int = 0,
    val bookmarkCount: Int = 0,
    val viewCount: Int = 0,
    val commentCount: Int = 0,

    val createdAt: Long = 0L
)