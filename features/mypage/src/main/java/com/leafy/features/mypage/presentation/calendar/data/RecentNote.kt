package com.leafy.features.mypage.presentation.calendar.data

import java.time.LocalDate

data class RecentNote(
    val id: String,
    val date: LocalDate,
    val timeOfDay: String,
    val content: String
)