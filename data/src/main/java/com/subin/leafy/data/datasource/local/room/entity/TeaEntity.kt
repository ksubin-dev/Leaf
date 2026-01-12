package com.subin.leafy.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teas")
data class TeaEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val name: String,
    val brand: String,
    val type: String,           // Enum Name 저장 ("GREEN", "BLACK")

    val origin: String,
    val leafStyle: String,
    val grade: String,

    val imageUrl: String?,
    val isFavorite: Boolean,    // 즐겨찾기
    val stockQuantity: String,  // "50g", "3개" 등 자유 입력
    val memo: String,

    val createdAt: Long
)