package com.leafy.shared.ui.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

    /**
     * 초(Int) -> "1분 30초" 또는 "30초" 형식으로 변환
     */
    fun formatSecondsToHangul(seconds: Int): String {
        val min = seconds / 60
        val sec = seconds % 60
        return if (min > 0) "${min}분 ${sec}초" else "${sec}초"
    }

    /**
     * 초(Int) -> "02:30" (디지털 타이머 형식) 변환
     */
    fun formatSecondsToTimer(seconds: Int): String {
        val min = seconds / 60
        val sec = seconds % 60
        return "%02d:%02d".format(min, sec)
    }
}