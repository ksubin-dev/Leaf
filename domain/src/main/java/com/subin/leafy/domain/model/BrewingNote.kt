package com.subin.leafy.domain.model

data class BrewingNote(
    val id: String,
    val ownerId: String,
    val isPublic: Boolean = false,
    val teaInfo: TeaInfo,
    val recipe: BrewingRecipe,
    val evaluation: SensoryEvaluation,
    val rating: RatingInfo,
    val metadata: NoteMetadata,
    val stats: PostStatistics,
    val myState: PostSocialState,
    val date: Long,
    val createdAt: Long,
)

data class TeaInfo(
    val name: String,
    val brand: String,
    val type: TeaType,
    val origin: String = "",
    val leafStyle: String = "",
    val grade: String = ""
)

data class BrewingRecipe(
    val waterTemp: Int,
    val leafAmount: Float,
    val waterAmount: Int,
    val brewTimeSeconds: Int,
    val infusionCount: Int,
    val teaware: TeawareType
)

data class SensoryEvaluation(
    val flavorTags: List<FlavorTag> = emptyList(),
    val sweetness: Int = 0,
    val sourness: Int = 0,
    val bitterness: Int = 0,
    val astringency: Int = 0,
    val umami: Int = 0,
    val body: BodyType = BodyType.MEDIUM,
    val finishLevel: Int = 0,
    val memo: String = ""
)

data class RatingInfo(
    val stars: Int = 0,
    val purchaseAgain: Boolean? = null
)

data class NoteMetadata(
    val weather: WeatherType? = null,
    val mood: String = "",
    val imageUrls: List<String> = emptyList()
)

