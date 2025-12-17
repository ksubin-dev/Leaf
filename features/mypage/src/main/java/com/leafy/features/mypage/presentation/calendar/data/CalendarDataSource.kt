package com.leafy.features.mypage.presentation.calendar.data

import android.os.Build
import java.time.LocalDate
import com.leafy.shared.R as SharedR

object CalendarDataSource {

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
}