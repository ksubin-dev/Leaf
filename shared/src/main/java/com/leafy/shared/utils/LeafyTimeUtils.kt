package com.leafy.shared.utils

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object LeafyTimeUtils {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun now(): LocalDate {
        return LocalDate.now()
    }

    fun getFirstDayOffset(yearMonth: YearMonth): Int {
        return yearMonth.atDay(1).dayOfWeek.value % 7
    }

    fun getDaysInMonth(yearMonth: YearMonth): Int {
        return yearMonth.lengthOfMonth()
    }

    fun nowToString(): String {
        return LocalDate.now().format(formatter)
    }

    fun millisToDateString(millis: Long): String {
        return Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(formatter)
    }

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

    fun parseToDate(dateString: String): LocalDate {
        return try {
            LocalDate.parse(dateString, formatter)
        } catch (e: Exception) {
            LocalDate.now()
        }
    }

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

    fun formatTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val minute = 60 * 1000L
        val hour = 60 * minute
        val day = 24 * hour

        return when {
            diff < minute -> "방금 전"
            diff < hour -> "${diff / minute}분 전"
            diff < 24 * hour -> "${diff / hour}시간 전"
            diff < 7 * day -> "${diff / day}일 전"
            else -> {
                val date = Date(timestamp)
                val format = SimpleDateFormat("MM.dd", Locale.getDefault())
                format.format(date)
            }
        }
    }
}