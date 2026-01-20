package com.leafy.shared.utils

/**
 * Int 타입의 숫자를 천 단위 이상일 때 'k'를 붙여 포맷하는 확장 함수.
 */
fun Int.toKiloFormat(): String {
    if (this < 1000) return this.toString()

    val count = (this.toDouble() / 1000)

    // 소수점 첫째 자리까지 표시하되, 정수이면 소수점(.0)을 생략 (예: 1000 -> 1k, 1200 -> 1.2k)
    return String.format(if (count % 1.0 == 0.0) "%.0fk" else "%.1fk", count)
}