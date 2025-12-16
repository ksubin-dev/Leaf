package com.leafy.features.badges.data

import androidx.annotation.DrawableRes

data class BadgeItem(
    val id: String,
    val title: String,
    val description: String,
    val isAcquired: Boolean,
    val progress: String? = null,
    @DrawableRes val iconRes: Int,
)