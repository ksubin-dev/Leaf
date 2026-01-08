package com.subin.leafy.domain.model

data class BrewingNote(
    val id: String,
    val ownerId: String,
    val isPublic: Boolean = false, // 공개 여부 (기본값 비공개)
    val teaInfo: TeaInfo,
    val recipe: BrewingRecipe,
    val evaluation: SensoryEvaluation,
    val rating: RatingInfo,
    val metadata: NoteMetadata,
    val stats: PostStatistics,    // 좋아요, 댓글 수 등 (기존 socialStats)
    val myState: PostSocialState,   // 내가 좋아요 했는지 등 (기존 isLiked 등)
    val createdAt: Long,
    val updatedAt: Long? = null
)

// 1. 차 정보
data class TeaInfo(
    val name: String,
    val brand: String,
    val type: TeaType,
    val origin: String = "",   // 산지 (대만, 중국 등)
    val leafStyle: String = "", // 찻잎 형태
    val grade: String = ""      // 등급 (OP, FOP 등)
)

// 2. 브루잉 레시피
data class BrewingRecipe(
    val waterTemp: Int,           // 온도 (예: 95)
    val leafAmount: Float,        // 찻잎 양 (예: 3.5f)
    val waterAmount: Int,         // 물의 양 (예: 150)
    val brewTimeSeconds: Int,     // 우린 시간 (예: 180)
    val infusionCount: Int,       // 우림 횟수 (예: 1)
    val teaware: String = ""
)

// 3. 맛 평가
data class SensoryEvaluation(
    val flavorNotes: Set<String> = emptySet(),
    val sweetness: Int = 0,       // 0~5 단계
    val sourness: Int = 0,
    val bitterness: Int = 0,
    val astringency: Int = 0,     // 떫은맛 (차의 핵심)
    val umami: Int = 0,
    val body: BodyType = BodyType.MEDIUM,
    val finishLevel: Float = 0.5f,
    val memo: String = ""
)

// 4. 별점 및 재구매 의사
data class RatingInfo(
    val stars: Int = 0,
    val purchaseAgain: Boolean? = null
)

// 5. 메타데이터
data class NoteMetadata(
    val weather: WeatherType? = null,
    val mood: String = "",
    val imageUrls: List<String> = emptyList()
)

