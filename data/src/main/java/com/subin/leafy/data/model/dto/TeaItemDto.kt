package com.subin.leafy.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class TeaItemDto(
    val id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val brand: String = "",
    val type: String = "ETC",
    val origin: String = "",
    val leafStyle: String = "",
    val grade: String = "",
    val imageUrl: String? = null,
    val isFavorite: Boolean = false,
    val stockQuantity: String = "",
    val memo: String = "",
    val createdAt: Long = System.currentTimeMillis()
)