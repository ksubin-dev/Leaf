package com.subin.leafy.data.model.dto

import com.google.firebase.firestore.PropertyName
import kotlinx.serialization.Serializable

@Serializable
data class BrewingNoteDto(
    val id: String = "",
    val ownerId: String = "",

    @get:PropertyName("public")
    @set:PropertyName("public")
    var isPublic: Boolean = false,

    // 1. TeaInfo
    val teaName: String = "",
    val teaBrand: String = "",
    val teaType: String = "",
    val teaOrigin: String = "",
    val teaLeafStyle: String = "",
    val teaGrade: String = "",

    // 2. BrewingRecipe
    val waterTemp: Int = 0,
    val leafAmount: Float = 0f,
    val waterAmount: Int = 0,
    val brewTimeSeconds: Int = 0,
    val infusionCount: Int = 1,
    val teaware: String = "",

    // 3. SensoryEvaluation
    val flavorNotes: List<String> = emptyList(),
    val sweetness: Int = 0,
    val sourness: Int = 0,
    val bitterness: Int = 0,
    val astringency: Int = 0,
    val umami: Int = 0,
    val bodyType: String = "MEDIUM",
    val finishLevel: Int = 0,
    val memo: String = "",

    // 4. Rating & Metadata
    val stars: Int = 0,
    val purchaseAgain: Boolean? = null,
    val weather: String? = null,
    val mood: String = "",
    val imageUrls: List<String> = emptyList(),

    // 5. SocialInteraction
    val likeCount: Int = 0,
    val bookmarkCount: Int = 0,
    val commentCount: Int = 0,
    val viewCount: Int = 0,

    val date: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)