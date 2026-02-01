package com.subin.leafy.domain.model

data class TeaItem(
    val id: String,
    val ownerId: String,
    val name: String,
    val brand: String,
    val type: TeaType,
    val origin: String = "",
    val leafStyle: String = "",
    val grade: String = "",
    val imageUrl: String? = null,
    val isFavorite: Boolean = false,
    val stockQuantity: String = "",
    val memo: String = "",
    val createdAt: Long = System.currentTimeMillis()
)