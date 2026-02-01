package com.subin.leafy.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timer_presets")
data class TimerPresetEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val teaType: String,
    val isDefault: Boolean,

    val waterTemp: Int,
    val brewTimeSeconds: Int,
    val leafAmount: Float,
    val waterAmount: Int,
    val infusionCount: Int,
    val teaware: String = ""
)