package com.leafy.features.community.ui.component

import androidx.annotation.DrawableRes

data class ExploreNoteSummaryUi(
    val title: String,
    val subtitle: String,
    @DrawableRes val imageRes: Int,
    val rating: Float,
    val savedCount: Int,
    @DrawableRes val profileImageRes: Int? = null,
    val authorName: String? = null,
    val likeCount: Int? = null,
    val isLiked: Boolean = false
)