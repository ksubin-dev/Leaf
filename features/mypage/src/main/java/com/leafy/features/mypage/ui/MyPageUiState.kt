package com.leafy.features.mypage.ui

import com.subin.leafy.domain.model.BrewingRecord
import com.subin.leafy.domain.model.User
import com.subin.leafy.domain.model.UserStats
import java.time.LocalDateTime
import java.time.YearMonth

data class MyPageUiState(
    val user: User? = null,
    val userStats: UserStats? = null,
    val selectedDateTime: LocalDateTime = LocalDateTime.now(),
    val recordedDays: List<Int> = emptyList(), // 기록이 있는 날짜들 (점 표시용)
    val monthlyRecords: List<BrewingRecord> = emptyList(), // 추가: 이번 달 전체 데이터
    val selectedRecord: BrewingRecord? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val currentMonth: YearMonth get() = YearMonth.from(selectedDateTime)
    val selectedDay: Int get() = selectedDateTime.dayOfMonth
    val selectedMonth: Int get() = selectedDateTime.monthValue
    val selectedYear: Int get() = selectedDateTime.year
}