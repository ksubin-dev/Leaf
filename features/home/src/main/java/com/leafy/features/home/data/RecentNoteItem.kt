package com.leafy.features.home.data

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

@Immutable
data class RecentNoteItem(
    val title: String,
    val rating: Double,
    @DrawableRes val imageRes: Int,
    val description: String
)