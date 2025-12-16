package com.leafy.features.analyze.data

data class TopTeaRanking(
    val rank: Int,
    val name: String,
    val count: Int,
    val rating: Float,
    val imageUrl: String
)