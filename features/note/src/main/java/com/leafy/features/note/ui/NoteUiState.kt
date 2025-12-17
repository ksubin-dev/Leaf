package com.leafy.features.note.ui

import com.subin.leafy.domain.model.WeatherType

/**
 * 브루잉 노트 작성 화면의 상태를 관리하는 객체
 */
data class NoteUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSaved: Boolean = false,

    val dateTime: String = "",
    val weather: WeatherType = WeatherType.CLEAR,
    val withPeople: String = "",

    val teaName: String = "",
    val brandName: String = "",
    val teaType: String = "Black",
    val leafStyle: String = "Loose Leaf",
    val leafProcessing: String = "Whole Leaf",
    val teaGrade: String = "OP",

    val waterTemp: String = "",
    val leafAmount: String = "",
    val brewTime: String = "",
    val brewCount: String = "1",
    val teaware: String = "찻주전자",

    val selectedTags: Set<String> = emptySet(),
    val sweetness: Int = 0,
    val sourness: Int = 0,
    val bitterness: Int = 0,
    val saltiness: Int = 0,
    val umami: Int = 0,
    val bodyIndex: Int = 1,
    val finishLevel: Float = 0.5f,

    val rating: Int = 0,
    val purchaseAgain: Boolean? = null,
    val memo: String = ""
) {
    val isError: Boolean get() = errorMessage != null
    val canSave: Boolean get() = teaName.isNotBlank() && !isLoading
}