package com.leafy.features.community.ui.component

import androidx.annotation.DrawableRes

data class ExploreTeaMasterUi(
    @DrawableRes val profileImageRes: Int,
    val name: String,
    val title: String,
    val isFollowing: Boolean = false
)
