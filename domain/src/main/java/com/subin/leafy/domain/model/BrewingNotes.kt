package com.subin.leafy.domain.model

import java.util.Date
import java.util.UUID

data class BrewingNote(
    val id: String,
    val ownerId : String,
    val teaInfo: TeaInfo,
    val condition: BrewingCondition,
    val evaluation: SensoryEvaluation,
    val ratingInfo: RatingInfo,
    val context: NoteContext,
    val createdAt: Date = Date(),
    val likeCount: Int = 0,
    val bookmarkCount: Int = 0,
    val viewCount: Int = 0,
    val commentCount: Int = 0,

    val isLiked: Boolean = false,
    val isBookmarked: Boolean = false
)

// 차에 대한 기본 정보
data class TeaInfo(
    val name: String,
    val brand: String,
    val type: String,          // Black, Green...
    val leafStyle: String,     // Loose Leaf...
    val processing: String,    // Whole Leaf...
    val grade: String          // OP, FOP...
)

// 타이머 모듈과 직결될 브루잉 조건
data class BrewingCondition(
    val waterTemp: String,
    val leafAmount: String,
    val brewTime: String,      // 타이머 모듈에서 전달받을 핵심 데이터
    val brewCount: String,     // 몇 번째 우림인지
    val teaware: String
)

// 맛 평가 정보 (SensoryEvaluationSection 대응)
data class SensoryEvaluation(
    val selectedTags: Set<String> = emptySet(),
    val sweetness: Float = 0f,
    val sourness: Float = 0f,
    val bitterness: Float = 0f,
    val saltiness: Float = 0f,
    val umami: Float = 0f,
    val bodyType: BodyType = BodyType.MEDIUM,
    val finishLevel: Float = 0.5f,
    val memo: String = ""
)

fun SensoryEvaluation.getFinishLabel(): String {
    return when {
        finishLevel < 0.3f -> "Clean (깔끔)"
        finishLevel > 0.7f -> "Astringent (떫음)"
        else -> "Balanced (균형 잡힘)"
    }
}

// 별점 및 재구매 의사
data class RatingInfo(
    val stars: Int = 0,
    val purchaseAgain: Boolean? = null
)

// 날씨 및 사진 정보
data class NoteContext(
    val dateTime: String = "",
    val weather: WeatherType,
    val withPeople: String = "",
    val dryLeafUri: String? = null,
    val liquorUri: String? = null,
    val teawareUri: String? = null,
    val additionalUri: String? = null
)
