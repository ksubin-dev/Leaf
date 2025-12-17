package com.leafy.features.mypage.presentation.collection.data

import androidx.annotation.DrawableRes

data class TeaCollectionItem(
    val id: String,
    val name: String,
    val brand: String,
    val quantity: String,
    @DrawableRes val imageRes: Int,
)