package com.subin.leafy.data.model.dto

data class TeaItemDTO(
    val id: String = "",
    val name: String = "",
    val brand: String = "",
    val type: String = "", // Green, Black, Oolong...
    val boughtDate: Long? = null,
    val expiryDate: Long? = null,
    val memo: String = "",
    val isFavorite: Boolean = false
)