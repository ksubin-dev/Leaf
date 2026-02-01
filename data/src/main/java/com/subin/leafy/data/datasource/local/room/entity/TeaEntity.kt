package com.subin.leafy.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teas")
data class TeaEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val name: String,
    val brand: String,
    val type: String,

    val origin: String,
    val leafStyle: String,
    val grade: String,

    val imageUrl: String?,
    val isFavorite: Boolean,
    val stockQuantity: String,
    val memo: String,

    val createdAt: Long
)