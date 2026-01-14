package com.subin.leafy.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val ownerId: String,
    val isPublic: Boolean,

    // 1. TeaInfo
    val teaName: String,
    val teaBrand: String,
    val teaType: String,
    val teaOrigin: String,
    val teaLeafStyle: String,
    val teaGrade: String,

    // 2. BrewingRecipe
    val waterTemp: Int,
    val leafAmount: Float,
    val waterAmount: Int,
    val brewTimeSeconds: Int,
    val infusionCount: Int,
    val teaware: String,

    // 3. SensoryEvaluation
    val flavorNotes: List<String>,
    val sweetness: Int,
    val sourness: Int,
    val bitterness: Int,
    val astringency: Int,
    val umami: Int,
    val bodyType: String,
    val finishLevel: Int,
    val memo: String,

    // 4. Rating & Metadata
    val stars: Int,
    val purchaseAgain: Boolean?,
    val weather: String?,
    val mood: String,
    val imageUrls: List<String>,

    // 5. Social
    val likeCount: Int,
    val bookmarkCount: Int,
    val commentCount: Int,
    val viewCount: Int,

    val date: Long,
    val createdAt: Long
)