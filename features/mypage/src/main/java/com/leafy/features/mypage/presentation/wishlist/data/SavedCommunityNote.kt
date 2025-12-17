package com.leafy.features.mypage.presentation.wishlist.data

import androidx.annotation.DrawableRes

data class SavedCommunityNote(
    val id: String,
    val title: String,
    val snippet: String,
    val rating: Float,
    val reviewCount: Int,
    @DrawableRes val teaImageRes: Int,
    @DrawableRes val profileImageRes: Int
)