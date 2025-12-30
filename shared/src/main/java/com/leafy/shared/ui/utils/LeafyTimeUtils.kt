package com.leafy.shared.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
object LeafyTimeUtils {
    // 1. 저장 및 비교용 표준 포맷 (시간 제외: yyyy-MM-dd)
    private val dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

    // 2. 화면 표시용 포맷 (시간 제외: Dec 30, 2025)
    private val displayFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH)

    /** 현재 날짜를 "yyyy-MM-dd" 형식으로 반환 (새 노트 작성 시 초기값) */
    fun nowToString(): String {
        return LocalDate.now().format(dbFormatter)
    }

    /** LocalDate 객체를 저장용 "yyyy-MM-dd" 문자열로 변환 */
    fun formatToString(date: LocalDate): String {
        return date.format(dbFormatter)
    }

    /** 저장된 "yyyy-MM-dd" 문자열을 LocalDate 객체로 변환 (Picker 초기값 설정 등) */
    fun parseToDate(dateString: String): LocalDate {
        return runCatching {
            LocalDate.parse(dateString, dbFormatter)
        }.getOrElse {
            // 에러 발생 시(포맷 불일치 등) 오늘 날짜 반환
            LocalDate.now()
        }
    }

    /** "2025-12-30" -> "Dec 30, 2025" (화면 표시용) */
    fun formatToDisplay(dateString: String): String {
        if (dateString.isBlank()) return ""
        val date = parseToDate(dateString)
        return date.format(displayFormatter)
    }

    /** Repository 검색용 접두사 생성 (예: "2025-12") */
    fun getMonthPrefix(year: Int, month: Int): String {
        return String.format(Locale.getDefault(), "%04d-%02d", year, month)
    }

    /** API 레벨에 상관없이 안전하게 LocalDate를 가져오는 헬퍼 */
    fun now(): LocalDate {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {
            val calendar = java.util.Calendar.getInstance()
            LocalDate.of(
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH) + 1,
                calendar.get(java.util.Calendar.DAY_OF_MONTH)
            )
        }
    }
}