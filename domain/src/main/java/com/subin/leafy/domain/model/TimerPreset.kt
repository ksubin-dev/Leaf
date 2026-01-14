package com.subin.leafy.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TimerPreset(
    val id: Long = 0,
    val name: String = "",
    val temp: String = "",
    val baseTimeSeconds: Int = 0,
    val leafAmount: String = "",
    val teaType: String = "Green"
)

@Serializable
data class InfusionRecord(
    val id: Long = System.currentTimeMillis(),
    val count: Int,          // 1, 2, 3... (몇 번째 우림인지)
    val timeSeconds: Int,    // 실제 우린 시간
    val formattedTime: String // "02:00"
)