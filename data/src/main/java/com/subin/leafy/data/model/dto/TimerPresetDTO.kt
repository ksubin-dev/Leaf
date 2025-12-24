package com.subin.leafy.data.model.dto

data class TimerPresetDTO(
    val id: String,
    val name: String,
    val temp: String,
    val time: Int,
    val amount: String,
    val type: String
)