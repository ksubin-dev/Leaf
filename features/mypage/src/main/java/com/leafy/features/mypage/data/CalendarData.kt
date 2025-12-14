package com.leafy.features.mypage.data

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import com.leafy.shared.R as SharedR
import java.time.LocalDate

/**
 * 차 시음 세션에 대한 데이터 구조
 */
@RequiresApi(Build.VERSION_CODES.O)
data class TeaSession(
    val id: String,
    val teaName: String,
    val teaOrigin: String,
    val rating: Double,
    @DrawableRes val imageRes: Int,
    val date: LocalDate
)

/**
 * 최근 기록된 시음 노트에 대한 데이터 구조
 */
data class RecentNote(
    val id: String,
    val date: LocalDate,
    val timeOfDay: String,
    val content: String
)

// ========= 임시 데이터 (이름 변경) =========

// 1. 세션 데이터
val DefaultTeaSessions by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        listOf(
            TeaSession("1", "Ceremonial Matcha", "Japan", 4.5, SharedR.drawable.img_matcha, LocalDate.of(2024, 12, 6)),
            TeaSession("2", "Tieguanyin", "China", 3.5, SharedR.drawable.img_tieguanyin, LocalDate.of(2024, 12, 6)),
        )
    } else {
        emptyList<TeaSession>()
    }
}

// 2. 노트 데이터
val DefaultRecentNotes by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        listOf(
            RecentNote("n1", LocalDate.of(2025, 12, 5), "Morning", "Perfect morning ritual..."),
            RecentNote("n2", LocalDate.of(2025, 12, 3), "Afternoon", "Tried the gongfu method..."),
        )
    } else {
        emptyList<RecentNote>()
    }
}

val DefaultDaysWithSessions: Set<Int> = setOf(
    1, 3, 4, 5, 6, 8, 10, 12, 13, 15,
)