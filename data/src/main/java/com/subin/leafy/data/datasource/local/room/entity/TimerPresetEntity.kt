package com.subin.leafy.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timer_presets")
data class TimerPresetEntity(
    @PrimaryKey
    val id: String,          // UUID
    val name: String,        // "잠 깨는 녹차"
    val teaType: String,     // ENUM name ("GREEN")
    val isDefault: Boolean,  // 기본 프리셋 여부

    // --- BrewingRecipe 필드 (Flatten) ---
    val waterTemp: Int,
    val brewTimeSeconds: Int,
    val leafAmount: Float,
    val waterAmount: Int,
    val infusionCount: Int,
    val teaware: String = ""
)