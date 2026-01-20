package com.leafy.shared.ui.utils

import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

object LeafyTimeUtils {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    // 캘린더용: 현재 날짜(LocalDate) 반환
    fun now(): LocalDate {
        return LocalDate.now()
    }

    // 캘린더용: 해당 월의 시작 요일 오프셋 (일요일=0, 월요일=1, ...)
    fun getFirstDayOffset(yearMonth: YearMonth): Int {
        // dayOfWeek.value: 월(1) ~ 일(7)
        // % 7 연산 결과: 일(0), 월(1) ... 토(6) -> 달력 UI(S M T W...)와 일치
        return yearMonth.atDay(1).dayOfWeek.value % 7
    }

    // 캘린더용: 해당 월의 총 일수
    fun getDaysInMonth(yearMonth: YearMonth): Int {
        return yearMonth.lengthOfMonth()
    }

    // 1. 현재 날짜 문자열
    fun nowToString(): String {
        return LocalDate.now().format(formatter)
    }

    // 2. Millis -> String
    fun millisToDateString(millis: Long): String {
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(formatter)
    }

    // 3. String -> Timestamp
    fun dateStringToTimestamp(dateString: String): Long {
        return try {
            LocalDate.parse(dateString, formatter)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }

    // 4. DatePicker 초기화
    fun parseToDate(dateString: String): LocalDate {
        return try {
            LocalDate.parse(dateString, formatter)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }

    // 5. 포맷팅
    fun formatToDisplay(dateString: String): String = dateString

    fun formatLongToString(timestamp: Long): String {
        return millisToDateString(timestamp)
    }

    fun formatSecondsToHangul(seconds: Int): String {
        val min = seconds / 60
        val sec = seconds % 60
        return if (min > 0) "${min}분 ${sec}초" else "${sec}초"
    }

    fun formatSecondsToTimer(seconds: Int): String {
        val min = seconds / 60
        val sec = seconds % 60
        return "%02d:%02d".format(min, sec)
    }

    fun getRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val minute = 60 * 1000L
        val hour = 60 * minute
        val day = 24 * hour

        return when {
            diff < minute -> "방금 전"
            diff < hour -> "${diff / minute}분 전"
            diff < day -> "${diff / hour}시간 전"
            diff < 7 * day -> "${diff / day}일 전"
            else -> {
                val date = Date(timestamp)
                val format = java.text.SimpleDateFormat("yyyy.MM.dd", java.util.Locale.getDefault())
                format.format(date)
            }
        }
    }
}