package com.leafy.features.home.data

import androidx.annotation.DrawableRes

data class RecentNoteItem(
    val title: String,
    val rating: Double,
    @DrawableRes val imageRes: Int,
    val description: String
)