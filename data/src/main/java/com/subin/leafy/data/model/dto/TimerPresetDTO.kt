package com.subin.leafy.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class TimerPresetDto(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val teaType: String = "ETC",
    val isDefault: Boolean = false,
    val waterTemp: Int = 90,
    val timeSeconds: Int = 180,
    val leafAmount: Float = 3f,
    val waterAmount: Int = 150,
    val infusionCount: Int = 1,
    val teaware: String = "다관"
)
