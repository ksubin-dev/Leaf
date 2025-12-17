package com.leafy.features.mypage.presentation.collection.data

data class TeaFilterType(
    val type: String,
    val isSelected: Boolean
)

val DefaultTeaFilters = listOf(
    "All", "Black", "Green", "Oolong", "White", "Pu-erh", "Herbal"
)