package com.leafy.shared.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
object LeafyTimeUtils {
    // 1. 데이터 저장 및 저장용 포맷 (정렬이 용이한 ISO 형식)
    private val fullFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.getDefault())

    // 2. 날짜만 표시하거나 비교할 때 쓰는 포맷
    private val dateOnlyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())

    // 3. 화면(Header 등)에 예쁘게 보여주기 위한 포맷
    private val displayFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy • h:mm a", Locale.ENGLISH)

    /** 현재 시간을 String으로 반환 (노트 저장 시 사용) */
    fun nowToString(): String {
        return LocalDateTime.now().format(fullFormatter)
    }

    /** LocalDateTime 객체를 저장용 String으로 변환 */
    fun formatToString(dateTime: LocalDateTime): String {
        return dateTime.format(fullFormatter)
    }

    /** 저장된 String을 LocalDateTime 객체로 변환 (UI 조작 시 사용) */
    fun parseToDateTime(dateString: String): LocalDateTime {
        return runCatching {
            LocalDateTime.parse(dateString, fullFormatter)
        }.getOrElse {
            // 포맷이 다르거나(yyyy-MM-dd만 있는 경우 등) 에러 시 현재 시간 반환
            runCatching {
                LocalDateTime.parse(dateString + " 00:00", fullFormatter)
            }.getOrDefault(LocalDateTime.now())
        }
    }

    /** "2024-11-20 14:30" -> "2024-11-20" 추출 (달력 날짜 비교용) */
    fun extractDateOnly(dateString: String): String {
        return if (dateString.length >= 10) dateString.substring(0, 10) else dateString
    }

    /** "2024-11-20 14:30" -> "Nov 20, 2024 • 2:30 PM" (상세 화면 표시용) */
    fun formatToDisplay(dateString: String): String {
        if (dateString.isBlank()) return ""
        val dateTime = parseToDateTime(dateString)
        return dateTime.format(displayFormatter)
    }

    /** 🎯 API 레벨에 상관없이 안전하게 LocalDateTime을 가져오는 헬퍼 */
    fun now(): LocalDateTime {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            // API 26 미만 기기를 위한 처리 (Calendar 활용 등)
            val calendar = java.util.Calendar.getInstance()
            LocalDateTime.of(
                calendar.get(java.util.Calendar.YEAR),
                calendar.get(java.util.Calendar.MONTH) + 1,
                calendar.get(java.util.Calendar.DAY_OF_MONTH),
                calendar.get(java.util.Calendar.HOUR_OF_DAY),
                calendar.get(java.util.Calendar.MINUTE)
            )
        }
    }
}
