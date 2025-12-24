package com.leafy.shared.ui.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object TimeUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    private val seoulZone = ZoneId.of("Asia/Seoul")
    @RequiresApi(Build.VERSION_CODES.O)
    private val defaultFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")


    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentFormattedTime(): String {
        val nowInSeoul = ZonedDateTime.ofInstant(Instant.now(), seoulZone)
        return nowInSeoul.format(defaultFormatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentTimestamp(): Long {
        return Instant.now().toEpochMilli()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTimestamp(timestamp: Long): String {
        val zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), seoulZone)
        return zonedDateTime.format(defaultFormatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun toLocalDate(timestamp: Long): java.time.LocalDate {
        return Instant.ofEpochMilli(timestamp).atZone(seoulZone).toLocalDate()
    }
}