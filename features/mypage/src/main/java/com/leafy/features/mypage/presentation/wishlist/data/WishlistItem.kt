package com.leafy.features.mypage.presentation.wishlist.data

import androidx.annotation.DrawableRes

data class WishlistItem(
    val id: String,
    val name: String,
    val originAndType: String,
    @DrawableRes val imageRes: Int,
)