package com.leafy.shared.ui.utils

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Instant

object LeafyTimeUtils {

    // 1. 저장 및 데이터베이스 비교용 표준 포맷 (yyyy-MM-dd)
    private val dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

    // 2. 화면 표시용 포맷 (예: Jan 02, 2026)
    private val displayFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)

    /** 현재 날짜를 "yyyy-MM-dd" 형식으로 반환 */
    fun nowToString(): String = LocalDate.now().format(dbFormatter)

    /** LocalDate 객체를 "yyyy-MM-dd" 문자열로 변환 */
    fun formatToString(date: LocalDate): String = date.format(dbFormatter)

    /** 저장된 문자열을 LocalDate 객체로 변환 (실패 시 오늘 날짜 반환) */
    fun parseToDate(dateString: String): LocalDate {
        return runCatching {
            LocalDate.parse(dateString, dbFormatter)
        }.getOrElse {
            LocalDate.now()
        }
    }

    /** "2026-01-02" -> "Jan 02, 2026" (UI 표시용) */
    fun formatToDisplay(dateString: String): String {
        if (dateString.isBlank()) return ""
        return parseToDate(dateString).format(displayFormatter)
    }



    /** Repository 월별 검색용 접두사 생성 (예: "2026-01") */
    fun getMonthPrefix(year: Int, month: Int): String {
        return String.format(Locale.getDefault(), "%04d-%02d", year, month)
    }

    /** 현재 날짜를 LocalDate 객체로 반환 */
    fun now(): LocalDate = LocalDate.now()

    fun getOneWeekAgo(): java.util.Date {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -7)
        return calendar.time
    }
}