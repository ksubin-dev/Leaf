package com.subin.leafy.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TimerPreset(
    val id: String,
    val name: String,           // 예: "나만의 우롱차 레시피"
    val teaType: TeaType,       // String -> TeaType Enum으로
    val temperature: Int,       // BrewingRecipe.waterTemp와 대응
    val baseTimeSeconds: Int,   // 기준 우림 시간 (초)
    val leafAmount: Float,      // BrewingRecipe.leafAmount와 대응
    val waterAmount: Int        // BrewingRecipe.waterAmount와 대응
)

@Serializable
data class InfusionRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val count: Int,          // 1, 2, 3... (몇 번째 우림인지)
    val timeSeconds: Int,    // 실제 우린 시간
    val formattedTime: String // "02:00"
)