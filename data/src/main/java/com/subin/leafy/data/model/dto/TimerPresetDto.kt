package com.subin.leafy.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class TimerPresetDto(
    val id: String = "",
    val userId: String = "",           // 누구의 프리셋인지
    val name: String = "",             // "아침용 진한 녹차"
    val teaType: String = "ETC",       // ENUM -> String
    val isDefault: Boolean = false,    // 기본 제공 여부
    val waterTemp: Int = 90,
    val timeSeconds: Int = 180,
    val leafAmount: Float = 3f,
    val waterAmount: Int = 150,        // 물 양 (레시피 필수값)
    val infusionCount: Int = 1         // 몇 번째 우림인지
)
