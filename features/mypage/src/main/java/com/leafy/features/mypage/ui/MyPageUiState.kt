package com.leafy.features.mypage.ui

import android.os.Build
import androidx.annotation.RequiresApi
import com.leafy.shared.ui.utils.LeafyTimeUtils
import com.subin.leafy.domain.model.BrewingRecord
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserStats
import java.time.LocalDate
import java.time.YearMonth

@RequiresApi(Build.VERSION_CODES.O)
data class MyPageUiState(
    val user: User? = null,
    val userStats: UserStats? = null,
    val selectedDate: LocalDate = LeafyTimeUtils.now(),
    val recordedDays: List<Int> = emptyList(),
    val monthlyRecords: List<BrewingRecord> = emptyList(),
    val selectedRecord: BrewingRecord? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val currentMonth: YearMonth
        get() = YearMonth.from(selectedDate)

    val selectedDay: Int
        get() = selectedDate.dayOfMonth

    val displayMonth: String
        get() = "${selectedDate.year}년 ${selectedDate.monthValue}월"

    fun hasRecordOnSelectedDay(): Boolean =
        recordedDays.contains(selectedDate.dayOfMonth)

    val selectedDateString: String
        get() = LeafyTimeUtils.formatToString(selectedDate)
}