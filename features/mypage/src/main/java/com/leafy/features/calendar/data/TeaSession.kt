package com.leafy.features.calendar.data

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
data class TeaSession(
    val id: String,
    val teaName: String,
    val teaOrigin: String,
    val rating: Double,
    @DrawableRes val imageRes: Int,
    val date: LocalDate
)