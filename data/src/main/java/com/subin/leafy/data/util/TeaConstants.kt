package com.subin.leafy.data.util

import com.subin.leafy.domain.model.TeaType

object TeaConstants {

    // 1. 평균 카페인 함량 (mg/잔) - 분석 계산용
    val CAFFEINE_MAP = mapOf(
        TeaType.MATCHA to 70, // 고카페인
        TeaType.PUERH to 65,
        TeaType.BLACK to 47,
        TeaType.OOLONG to 37,
        TeaType.YELLOW to 30,
        TeaType.GREEN to 28,
        TeaType.WHITE to 28,
        TeaType.HERBAL to 0,
        TeaType.ETC to 0
    )

    // 2. 카페인 범위 문자열 (UI 표시용) - 필요하면 꺼내 쓰기
    fun getCaffeineRange(type: TeaType): String {
        return when(type) {
            TeaType.MATCHA -> "60~80mg (고카페인)"
            TeaType.PUERH -> "30~70mg"
            TeaType.BLACK -> "40~70mg"
            TeaType.OOLONG -> "30~50mg"
            TeaType.GREEN, TeaType.YELLOW -> "20~45mg"
            TeaType.WHITE -> "15~55mg"
            TeaType.HERBAL -> "0mg"
            TeaType.ETC -> "-"
        }
    }

    // 안전하게 값 꺼내는 헬퍼 함수
    fun getCaffeineAmount(typeString: String): Int {
        val type = runCatching { TeaType.valueOf(typeString) }.getOrDefault(TeaType.ETC)
        return CAFFEINE_MAP[type] ?: 0
    }
}
