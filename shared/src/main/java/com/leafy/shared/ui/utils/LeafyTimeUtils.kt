package com.leafy.shared.ui.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object LeafyTimeUtils {
    private val seoulZone = ZoneId.of("Asia/Seoul")

    fun now(): LocalDateTime {
        return LocalDateTime.now(seoulZone)
    }

    fun fromTimestamp(timestamp: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), seoulZone)
    }

    fun toTimestamp(localDateTime: LocalDateTime): Long {
        return localDateTime.atZone(seoulZone).toInstant().toEpochMilli()
    }
}