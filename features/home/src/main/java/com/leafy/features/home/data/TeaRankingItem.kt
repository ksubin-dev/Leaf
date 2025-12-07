package com.leafy.features.home.data

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class TeaRankingItem(
    val rank: Int,
    val name: String,
    val typeCountry: String,
    val rating: Double,
    val ratingCount: Int,
    @DrawableRes val imageRes: Int,
    val badgeColor: Color
)