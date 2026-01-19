package com.subin.leafy.domain.model

import java.util.UUID

data class TimerPreset(
    val id: String,
    val name: String,           // 예: "나만의 우롱차 레시피", "잠 깨는 녹차"
    val teaType: TeaType,       // 아이콘 표시용
    val recipe: BrewingRecipe,
    val isDefault: Boolean = false // 기본 제공 프리셋인지 (삭제 방지용)
)


data class InfusionRecord(
    val id: String = UUID.randomUUID().toString(),
    val count: Int,
    val timeSeconds: Int,
    val waterTemp: Int,
    val timestamp: Long = System.currentTimeMillis()
)


data class TimerSettings(
    val isVibrationOn: Boolean = true,  // 진동 사용 여부
    val isSoundOn: Boolean = true,      // 소리 사용 여부
    val soundFileName: String = "bell_1", // 소리 종류 (나중에 여러 개 지원 대비)
    val keepScreenOn: Boolean = true    // 타이머 돌 때 화면 안 꺼지게 할지
)