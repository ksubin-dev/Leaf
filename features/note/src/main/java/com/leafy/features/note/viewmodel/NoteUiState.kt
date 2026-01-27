package com.leafy.features.note.viewmodel

import android.net.Uri
import com.leafy.shared.utils.LeafyTimeUtils
import com.subin.leafy.domain.model.BodyType
import com.subin.leafy.domain.model.FlavorTag
import com.subin.leafy.domain.model.TeaType
import com.subin.leafy.domain.model.TeawareType
import com.subin.leafy.domain.model.WeatherType

data class NoteUiState(
    val noteId: String? = null,
    val originalCreatedAt: Long? = null,

    val teaName: String = "",
    val teaBrand: String = "",
    val teaType: TeaType = TeaType.GREEN,
    val teaOrigin: String = "",
    val teaLeafStyle: String = "",
    val teaGrade: String = "",

    val waterTemp: String = "90",
    val leafAmount: String = "3",
    val waterAmount: String = "150",
    val brewTime: String = "180",
    val infusionCount: String = "1",
    val teaware: TeawareType = TeawareType.MUG,

    val flavorTags: List<FlavorTag> = emptyList(),
    val sweetness: Float = 0f,
    val sourness: Float = 0f,
    val bitterness: Float = 0f,
    val astringency: Float = 0f,
    val umami: Float = 0f,
    val body: BodyType = BodyType.MEDIUM,
    val finish: Float = 0f,
    val memo: String = "",

    val selectedDateString: String = LeafyTimeUtils.nowToString(),
    val selectedWeather: WeatherType? = null,
    val withPeople: String = "",

    val starRating: Int = 3,
    val purchaseAgain: Boolean? = null,
    val selectedImages: List<Uri> = emptyList(),

    val isPublic: Boolean = false,
    val isLoading: Boolean = false,

) {
    val isFormValid: Boolean
        get() = teaName.isNotBlank() &&
                waterTemp.toIntOrNull() != null &&
                brewTime.toIntOrNull() != null &&
                leafAmount.toFloatOrNull() != null &&
                waterAmount.toIntOrNull() != null &&
                selectedImages.isNotEmpty()
}