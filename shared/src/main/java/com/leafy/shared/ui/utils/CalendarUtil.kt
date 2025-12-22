package com.leafy.shared.ui.utils

import java.time.YearMonth

object CalendarUtil {
    /**
     * 캘린더 그리드에서 1일이 시작되기 전까지의 빈 칸(Offset) 개수를 계산합니다.
     * 기준: 일요일 시작 캘린더 (S:0, M:1, T:2, W:3, T:4, F:5, S:6)
     */
    fun getFirstDayOffset(currentMonth: YearMonth): Int {
        val firstDayOfMonth = currentMonth.atDay(1)
        // java.time.DayOfWeek는 월(1) ~ 일(7)을 반환합니다.
        // 일요일 시작 기준(0)으로 바꾸기 위해 나머지를 연산합니다.
        // (월:1%7=1, ..., 토:6%7=6, 일:7%7=0)
        return firstDayOfMonth.dayOfWeek.value % 7
    }

    /**
     * 해당 월의 총 일수(길이)를 반환합니다. (예: 28, 30, 31일)
     */
    fun getDaysInMonth(currentMonth: YearMonth): Int {
        return currentMonth.lengthOfMonth()
    }
}