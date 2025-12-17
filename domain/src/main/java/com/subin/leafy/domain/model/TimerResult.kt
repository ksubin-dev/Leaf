package com.subin.leafy.domain.model

data class TimerResult(
    val totalBrewTime: String,  // 예: "02:30"
    val infusionCount: Int,     // 예: 2
    val teaName: String? = null // 타이머 시작 시 선택했던 차 이름이 있다면
)