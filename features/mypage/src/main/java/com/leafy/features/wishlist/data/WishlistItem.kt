package com.leafy.features.wishlist.data

import androidx.annotation.DrawableRes

data class WishlistItem(
    val id: String,
    val name: String,
    val originAndType: String,
    @DrawableRes val imageRes: Int,
)