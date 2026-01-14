package com.leafy.shared.ui.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

object LeafyTimeUtils {

    // 표준 포맷 (yyyy-MM-dd)
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    // 1. [초기값] 현재 날짜를 문자열로 반환
    fun nowToString(): String {
        return LocalDate.now().format(formatter)
    }

    // 2. [UI 업데이트] Millis(Long) -> "yyyy-MM-dd" 변환
    fun millisToDateString(millis: Long): String {
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(formatter)
    }

    // 3. [DB 저장] "yyyy-MM-dd" -> Timestamp(Long) 변환
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

    // 4. [DatePicker 초기화]
    fun parseToDate(dateString: String): LocalDate {
        return try {
            LocalDate.parse(dateString, formatter)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }

    // 5. [UI 표시]
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