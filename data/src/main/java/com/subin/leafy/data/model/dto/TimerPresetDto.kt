package com.subin.leafy.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class TimerPresetDto(
    val id: String = "",
    val userId: String = "",           // 누구의 프리셋인지 구분
    val name: String = "",             // 프리셋 이름 (예: "아침 우롱차")
    val waterTemp: Int = 0,            // temp: String -> waterTemp: Int (단위 통일)
    val timeSeconds: Int = 0,          // time: Int -> timeSeconds (단위 명시)
    val leafAmount: Float = 0f,        // amount: String -> leafAmount: Float
    val teaType: String = ""           // type -> teaType
)