package com.subin.leafy.data.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class TeaMasterDto(
    val id: String = "",
    val name: String = "",
    val title: String = "",
    val profileImageUrl: String? = null
)